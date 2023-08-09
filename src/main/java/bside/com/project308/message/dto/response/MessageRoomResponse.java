package bside.com.project308.message.dto.response;

import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.SimpleMemberInfo;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.MessageRoomDto;

import java.time.LocalDateTime;

public record MessageRoomResponse(
        Long id,
        SimpleMemberInfo fromMember,
        SimpleMemberInfo toMember,
        LocalDateTime createdTime,
        MessageResponse lastMessage
) {

    public static MessageRoomResponse from(MessageRoomDto messageRoomDto) {
        MemberDto fromMember = messageRoomDto.fromMember();
        MemberDto toMember = messageRoomDto.toMember();
        return new MessageRoomResponse(
                messageRoomDto.id(),
                new SimpleMemberInfo(fromMember.id(), fromMember.username(), fromMember.position(), fromMember.imgUrl()),
                new SimpleMemberInfo(toMember.id(), toMember.username(), toMember.position(), toMember.imgUrl()),
                messageRoomDto.createdTime(),
                MessageResponse.from(messageRoomDto.lastMessage()));
    }
}
