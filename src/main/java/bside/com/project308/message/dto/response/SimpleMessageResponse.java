package bside.com.project308.message.dto.response;

import bside.com.project308.message.dto.MessageDto;


import java.time.LocalDateTime;

public record SimpleMessageResponse(Long id,
                                    String content,
                                    Long messageWriterId,
                                    boolean isRead,
                                    LocalDateTime messageCreatedTime) {

    public static SimpleMessageResponse from(MessageDto messageDto) {
        return new SimpleMessageResponse(messageDto.id(), messageDto.content(), messageDto.messageWriter().id(), messageDto.isRead(), messageDto.messageCreatedTime());
    }
}
