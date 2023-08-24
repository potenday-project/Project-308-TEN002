package bside.com.project308.notification.dto;

public record MessageNotificationResponse(Long writerId,
                                          Long messageId,
                                          Long messageRoomId,
                                          String messageContent) {
}
