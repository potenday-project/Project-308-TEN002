package bside.com.project308.message.dto.response;

import bside.com.project308.message.dto.MessageDto;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        Long messageWriterId,
        boolean isRead,
        LocalDateTime messageCreatedTime
) {

    public static MessageResponse from(MessageDto messageDto) {
        return new MessageResponse(messageDto.id(), messageDto.content(), messageDto.messageWriterId(), messageDto.isRead(), messageDto.messageCreatedTime() );
    }

}
