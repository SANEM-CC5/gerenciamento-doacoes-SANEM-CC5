package com.javalovers.service;

import com.javalovers.exception.EntityNotFoundException;
import com.javalovers.model.entity.AppUser;
import com.javalovers.repository.AppUserRepository;
import com.javalovers.model.dto.NotificationDTO;
import com.javalovers.model.entity.Notification;
import com.javalovers.model.enums.NotificationType;
import com.javalovers.mapper.NotificationDTOMapper;
import com.javalovers.repository.NotificationRepository;
import com.javalovers.model.entity.StockAlert;
import com.javalovers.model.enums.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationDTOMapper notificationDTOMapper;

    /**
     * Perfis que recebem alertas de estoque mínimo. Configurável em
     * application.yml (sanem.notifications.low-stock-profiles).
     */
    @Value("${sanem.notifications.low-stock-profiles:Administrator,Attendant}")
    private List<String> lowStockProfiles;

    /** Usuários ativos, não excluídos, com perfil autorizado a receber alertas de estoque. */
    public List<AppUser> findLowStockRecipients() {
        List<String> profiles = lowStockProfiles.stream()
                .map(p -> p.trim().toLowerCase(Locale.ROOT))
                .filter(p -> !p.isEmpty())
                .toList();
        if (profiles.isEmpty()) return List.of();
        return appUserRepository.findActiveByProfileNames(Status.ACTIVE, profiles);
    }

    /** Cria uma notificação de estoque baixo para cada usuário autorizado. Retorna quantos foram notificados. */
    @Transactional
    public int notifyLowStock(StockAlert alert) {
        List<AppUser> recipients = findLowStockRecipients();
        if (recipients.isEmpty()) return 0;

        String itemName = alert.getItem().getDescription();
        String title = "Estoque baixo: " + itemName;
        String message = alert.getStockQuantity() == 0
                ? String.format("O item \"%s\" está sem estoque (mínimo configurado: %d).",
                        itemName, alert.getMinimumStock())
                : String.format("O item \"%s\" atingiu o estoque mínimo: %d disponível(is), mínimo configurado: %d.",
                        itemName, alert.getStockQuantity(), alert.getMinimumStock());

        List<Notification> notifications = recipients.stream().map(user -> {
            Notification n = new Notification();
            n.setUser(user);
            n.setStockAlert(alert);
            n.setType(NotificationType.LOW_STOCK);
            n.setTitle(truncate(title, 150));
            n.setMessage(truncate(message, 500));
            return n;
        }).toList();

        notificationRepository.saveAll(notifications);
        return notifications.size();
    }

    @Transactional(readOnly = true)
    public List<NotificationDTO> listForUser(Long userId, boolean unreadOnly, int limit) {
        PageRequest page = PageRequest.of(0, Math.max(1, Math.min(limit, 100)));
        List<Notification> list = unreadOnly
                ? notificationRepository.findByUserUserIdAndReadAtIsNullOrderByCreatedAtDesc(userId, page)
                : notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId, page);
        return list.stream().map(notificationDTOMapper::convert).toList();
    }

    public long countUnread(Long userId) {
        return notificationRepository.countByUserUserIdAndReadAtIsNull(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository
                .findByNotificationIdAndUserUserId(notificationId, userId)
                .orElseThrow(() -> new EntityNotFoundException("notificação", notificationId));
        notification.markAsRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public int markAllAsRead(Long userId) {
        return notificationRepository.markAllAsRead(userId, LocalDateTime.now());
    }

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max - 1) + "…";
    }
}
