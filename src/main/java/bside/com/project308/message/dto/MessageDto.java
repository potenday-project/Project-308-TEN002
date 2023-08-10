package bside.com.project308.message.dto;

import bside.com.project308.message.entity.Message;

import java.time.LocalDateTime;

public record MessageDto (
        Long id,
        String content,
        Long messageRoomId,
        Long messageWriterId,
        boolean isRead,
        LocalDateTime messageCreatedTime
){



    public static MessageDto from(Message message) {

        Long messageWriterId = message.isFromMemberMessage() ? message.getMessageRoom().getFromMember().getId()
                : message.getMessageRoom().getToMember().getId();
        return new MessageDto(message.getId(), message.getContent(), message.getMessageRoom().getId(), messageWriterId, message.isRead(), message.getMessageCreatedTime());
    }

    public static MessageDto defaultMessage(Long messageRoomId, Long messageWriterId) {
        return new MessageDto(null, "새로운 매치입니다.", messageRoomId, messageWriterId, false, LocalDateTime.now());
    }
}
