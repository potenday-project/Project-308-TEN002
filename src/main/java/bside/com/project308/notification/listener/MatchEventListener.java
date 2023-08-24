package bside.com.project308.notification.listener;

import bside.com.project308.match.event.MatchEvent;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.notification.constant.NotificationType;
import bside.com.project308.notification.dto.MatchNotificationResponse;
import bside.com.project308.notification.dto.NotificationResponse;
import bside.com.project308.notification.entity.MatchNotification;
import bside.com.project308.notification.repository.MatchNotificationRepository;
import bside.com.project308.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MatchEventListener {

    private final SseService sseService;
    private final MatchNotificationRepository matchNotificationRepository;

    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void sendMatchNotification(MatchEvent matchEvent) {

        MatchDto newMatch = matchEvent.getMatch();

        MatchNotification matchToMemberNotification = MatchNotification.builder().matchId(newMatch.id())
                                                               .isRead(false)
                                                               .receiverId(newMatch.toMember().id())
                                                               .build();

        MatchNotification matchFromMemberNotification = MatchNotification.builder().matchId(newMatch.id())
                                                               .isRead(false)
                                                               .receiverId(newMatch.fromMember().id())
                                                               .build();

        matchNotificationRepository.saveAll(List.of(matchToMemberNotification, matchFromMemberNotification));
        MatchNotificationResponse matchResponse = new MatchNotificationResponse(MatchResponse.from(matchEvent.getMatch()),
                                                                            matchEvent.getMessageRoom().id());
        NotificationResponse notificationResponse = new NotificationResponse<MatchNotificationResponse>(null, NotificationType.MATCH, matchResponse);

        sseService.send(matchEvent.getMatch().toMember().id(), notificationResponse);
        sseService.send(matchEvent.getMatch().fromMember().id(), notificationResponse);
    }


}
