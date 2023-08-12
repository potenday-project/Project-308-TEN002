package bside.com.project308.message.dto.response;

import bside.com.project308.message.dto.MessageDto;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        MessageDto.MessageWriter messageWriter,
        boolean isRead,
        LocalDateTime messageCreatedTime
) {

    public static MessageResponse from(MessageDto messageDto) {
        return new MessageResponse(messageDto.id(),
                messageDto.content(),
                messageDto.messageWriter(),
                messageDto.isRead(),
                messageDto.messageCreatedTime() );
    }


}
