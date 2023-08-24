package bside.com.project308.message.event;

import bside.com.project308.message.controller.usecase.WriteMessageAndUpdateLastMessage;
import bside.com.project308.message.dto.MessageDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class NewMessageEvent extends ApplicationEvent {
    private final MessageDto messageDto;
    private final Long messageReceiverId;
    public NewMessageEvent(WriteMessageAndUpdateLastMessage source, MessageDto messageDto, Long messageReceiverId) {
        super(source);
        this.messageDto = messageDto;
        this.messageReceiverId = messageReceiverId;
    }
}
