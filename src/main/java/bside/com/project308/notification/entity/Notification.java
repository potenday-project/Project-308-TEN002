package bside.com.project308.notification.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import bside.com.project308.notification.constant.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
public abstract class Notification extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "notification_id")
    private Long id;
    private boolean isRead;
    private Long receiverId;

    public Notification(boolean isRead, Long receiverId) {
        this.isRead = isRead;
        this.receiverId = receiverId;
    }

    public void readNotification() {
        this.isRead = true;
    }
}
