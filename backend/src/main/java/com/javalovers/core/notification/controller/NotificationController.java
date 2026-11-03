package com.javalovers.core.notification.controller;

import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.service.AuthService;
import com.javalovers.core.notification.domain.dto.response.NotificationDTO;
import com.javalovers.core.notification.domain.dto.response.NotificationSummaryDTO;
import com.javalovers.core.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Notificações do usuário autenticado. Cada usuário só enxerga as próprias
 * notificações (o usuário é obtido do token JWT, nunca de parâmetro).
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> list(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            @RequestParam(defaultValue = "20") int limit) {
        AppUser user = authService.getCurrentUser();
        return ResponseEntity.ok(notificationService.listForUser(user.getUserId(), unreadOnly, limit));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<NotificationSummaryDTO> unreadCount() {
        AppUser user = authService.getCurrentUser();
        return ResponseEntity.ok(new NotificationSummaryDTO(notificationService.countUnread(user.getUserId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        AppUser user = authService.getCurrentUser();
        notificationService.markAsRead(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        AppUser user = authService.getCurrentUser();
        notificationService.markAllAsRead(user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
