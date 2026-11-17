package com.sanem.model.dto;

import com.sanem.model.enums.NotificationType;

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
