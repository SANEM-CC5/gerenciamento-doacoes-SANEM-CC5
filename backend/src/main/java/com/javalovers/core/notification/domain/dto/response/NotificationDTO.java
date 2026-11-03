package com.javalovers.core.notification.domain.dto.response;

import com.javalovers.core.notification.domain.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationDTO(
        Long notificationId,
        NotificationType type,
        String title,
        String message,
        LocalDateTime createdAt,
        LocalDateTime readAt,
        boolean read,
        Long itemId,
        String itemDescription,
        Long currentStock,
        Long minimumStock,
        /** true enquanto o item continuar com saldo <= mínimo. */
        boolean alertOpen
) {
}
