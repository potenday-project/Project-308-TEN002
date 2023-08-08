package bside.com.project308.message.dto;

import bside.com.project308.message.entity.Message;

public record MessageDto (
        Long id,
        String content,
        Long messageRoomId,
        boolean isFromMemberMessage,
        boolean isRead
){



    public static MessageDto from(Message message) {
        return new MessageDto(message.getId(), message.getContent(), message.getMessageRoom().getId(), message.isFromMemberMessage(), message.isRead());
    }
}
