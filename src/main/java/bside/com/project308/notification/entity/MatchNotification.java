package bside.com.project308.notification.entity;

import bside.com.project308.member.entity.Member;
import jakarta.persistence.Entity;
import lombok.Builder;

@Entity
public class MatchNotification extends Notification{
    private Long matchId;

    @Builder
    public MatchNotification(boolean isRead, Long receiverId, Long matchId) {
        super(isRead, receiverId);
        this.matchId = matchId;
    }
}

