package bside.com.project308.message.controller.usecase;

import bside.com.project308.match.event.MatchEvent;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.event.NewMessageEvent;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class WriteMessageAndUpdateLastMessage {
    private final MessageRoomService messageRoomService;
    private final MessageService messageService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void execute(Long memberId, MessageRequest messageRequest){
        MessageRoom messageRoom = messageRoomService.getMessageRoom(messageRequest.messageRoomId());
        Message message = messageService.writeMessage(messageRoom, memberId, messageRequest.content());
        messageRoom.updateLastMessage(message);
        notifyNewMessage(MessageDto.from(message), message.getMessageReceiverId());
    }

    private void notifyNewMessage(MessageDto messageDto, Long messageReceiverId) {
        eventPublisher.publishEvent(new NewMessageEvent(this, messageDto, messageReceiverId));
    }
}
