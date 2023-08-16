package bside.com.project308.message.dto;

import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.Message;

import java.time.LocalDateTime;

public record MessageDto (
        Long id,
        String content,
        Long messageRoomId,
        MessageWriter messageWriter,
        boolean isRead,
        LocalDateTime messageCreatedTime
){



    public static MessageDto from(Message message) {

        Member messageWriter = message.isFromMemberMessage() ? message.getMessageRoom().getFromMember()
                : message.getMessageRoom().getToMember();
        return new MessageDto(message.getId(), message.getContent(), message.getMessageRoom().getId(), MessageWriter.from(messageWriter), message.isRead(), message.getMessageCreatedTime());
    }

    public static MessageDto defaultMessage(Long messageRoomId, Member messageWriter) {
        return new MessageDto(null, "새로운 매치입니다.", messageRoomId, MessageWriter.from(messageWriter), false, LocalDateTime.now());
    }

    public record MessageWriter(Long id,
                         String username,
                         String imgUrl){

        public static MessageWriter from(Member member){
            return new MessageWriter(member.getId(), member.getUsername(), member.getImgUrl());
        }

    }
}
