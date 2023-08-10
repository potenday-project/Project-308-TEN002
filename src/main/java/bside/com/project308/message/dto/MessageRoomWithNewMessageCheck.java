package bside.com.project308.message.dto;

import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.message.entity.MessageRoom;

import java.time.LocalDateTime;

public record MessageRoomWithNewMessageCheck(Long id,
                                             Long loginMemberId,
                                             MemberDto fromMember,
                                             MemberDto toMember,
                                             LocalDateTime createdTime,
                                             MessageDto lastMessage,
                                             boolean isNewMessageInRoom

){
    public static MessageRoomWithNewMessageCheck from(Long loginMemberId, MessageRoom messageRoom, boolean isNewMessageInRoom) {
        return new MessageRoomWithNewMessageCheck(messageRoom.getId(),
                loginMemberId,
                MemberDto.from(messageRoom.getFromMember()),
                MemberDto.from(messageRoom.getToMember()),
                messageRoom.getCreatedTime(),
                MessageDto.from(messageRoom.getLastMessage()),
                isNewMessageInRoom);
    }

    public static MessageRoomWithNewMessageCheck from(Long loginMemberId, MessageRoom messageRoom, MessageDto defaultMessage, boolean isNewMessageInRoom) {
        return new MessageRoomWithNewMessageCheck(messageRoom.getId(),
                loginMemberId,
                MemberDto.from(messageRoom.getFromMember()),
                MemberDto.from(messageRoom.getToMember()),
                messageRoom.getCreatedTime(),
                defaultMessage,
                isNewMessageInRoom);
    }

    public static MessageRoomWithNewMessageCheck fromDto(Long loginMemberId, MessageRoomDto messageRoom, boolean isNewMessageInRoom) {
        return new MessageRoomWithNewMessageCheck(messageRoom.id(),
                loginMemberId,
                messageRoom.fromMember(),
                messageRoom.fromMember(),
                messageRoom.createdTime(),
                messageRoom.lastMessage(),
                isNewMessageInRoom);
    }

    public static MessageRoomWithNewMessageCheck fromDto(Long loginMemberId, MessageRoomDto messageRoom, MessageDto lastMessage, boolean isNewMessageInRoom) {
        return new MessageRoomWithNewMessageCheck(messageRoom.id(),
                loginMemberId,
                messageRoom.fromMember(),
                messageRoom.fromMember(),
                messageRoom.createdTime(),
                messageRoom.lastMessage(),
                isNewMessageInRoom);
    }

}
