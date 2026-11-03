package com.javalovers.core.notification.mapper;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.notification.domain.dto.response.NotificationDTO;
import com.javalovers.core.notification.domain.entity.Notification;
import com.javalovers.core.stockalert.domain.entity.StockAlert;
import org.springframework.stereotype.Service;

@Service
public class NotificationDTOMapper {

    public NotificationDTO convert(Notification notification) {
        if (notification == null) return null;

        StockAlert alert = notification.getStockAlert();
        Item item = alert != null ? alert.getItem() : null;

        return new NotificationDTO(
                notification.getNotificationId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.isRead(),
                item != null ? item.getItemId() : null,
                item != null ? item.getDescription() : null,
                item != null ? item.getStockQuantity() : null,
                item != null ? item.getMinimumStock() : null,
                alert != null && alert.isOpen()
        );
    }
}
