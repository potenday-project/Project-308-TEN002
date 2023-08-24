package bside.com.project308.notification.repository;

import bside.com.project308.notification.entity.MessageNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageNotificationRepository extends JpaRepository<MessageNotification, Long> {
}
