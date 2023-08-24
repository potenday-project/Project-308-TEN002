package bside.com.project308.notification.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Builder;

@Entity
public class MessageNotification extends Notification{

    private Long messageId;

    @Builder
    public MessageNotification(boolean isRead, Long receiverId, Long messageId) {
        super(isRead, receiverId);
        this.messageId = messageId;
    }
}
