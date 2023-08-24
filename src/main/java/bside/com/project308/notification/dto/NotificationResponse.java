package bside.com.project308.notification.dto;

import bside.com.project308.notification.constant.NotificationType;

public record NotificationResponse<T>(String eventId,
                                   NotificationType type,
                                   T content) {
}
