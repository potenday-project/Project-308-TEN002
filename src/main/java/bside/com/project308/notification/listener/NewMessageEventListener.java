package bside.com.project308.notification.listener;

import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.match.event.MatchEvent;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.response.MessageResponse;
import bside.com.project308.message.event.NewMessageEvent;
import bside.com.project308.notification.constant.NotificationType;
import bside.com.project308.notification.dto.MatchNotificationResponse;
import bside.com.project308.notification.dto.MessageNotificationResponse;
import bside.com.project308.notification.dto.NotificationResponse;
import bside.com.project308.notification.entity.MatchNotification;
import bside.com.project308.notification.entity.MessageNotification;
import bside.com.project308.notification.repository.MessageNotificationRepository;
import bside.com.project308.notification.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class NewMessageEventListener {

    private final SseService sseService;
    private final MessageNotificationRepository messageNotificationRepository;
    @TransactionalEventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void sendNewMessageNotification(NewMessageEvent newMessageEvent) {

        MessageDto messageDto = newMessageEvent.getMessageDto();


        MessageNotification messageNotification = MessageNotification.builder().messageId(messageDto.id())
                                                       .receiverId(newMessageEvent.getMessageReceiverId())
                                                       .isRead(false)
                                                       .build();
        messageNotificationRepository.save(messageNotification);

        MessageNotificationResponse messageNotificationResponse = new MessageNotificationResponse(messageDto.messageWriter().id(),
                messageDto.id(),
                messageDto.messageRoomId(),
                messageDto.content());
        log.debug("new message event");
        NotificationResponse notificationResponse = new NotificationResponse<MessageNotificationResponse>(null, NotificationType.MESSAGE, messageNotificationResponse);

        sseService.send(newMessageEvent.getMessageReceiverId(), notificationResponse);
    }
}
