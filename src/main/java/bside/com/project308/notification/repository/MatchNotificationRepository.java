package bside.com.project308.notification.repository;

import bside.com.project308.notification.entity.MatchNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchNotificationRepository extends JpaRepository<MatchNotification, Long> {
    Optional<MatchNotification> findByMatchIdAndReceiverId(Long matchId, Long receiverId);
}
