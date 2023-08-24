package bside.com.project308.notification.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.notification.entity.MatchNotification;
import bside.com.project308.notification.repository.MatchNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MatchNotificationService {

    private final MatchNotificationRepository matchNotificationRepository;

    public void checkMatch(Long memberId, Long matchId) {
        MatchNotification matchNotification = matchNotificationRepository
                                                            .findByMatchIdAndReceiverId(memberId, matchId)
                                                            .orElseGet(() -> MatchNotification.builder()
                                                                                              .matchId(matchId)
                                                                                              .receiverId(memberId)
                                                                                              .isRead(true)
                                                                                              .build());

        if (matchNotification.getId() == null) {
            matchNotificationRepository.save(matchNotification);
            return;
        }

        matchNotification.readNotification();
    }
}
