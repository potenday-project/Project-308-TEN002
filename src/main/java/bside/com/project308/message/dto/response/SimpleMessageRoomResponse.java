package bside.com.project308.message.dto.response;

import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.SimpleMemberInfo;
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;

import java.time.LocalDateTime;

public record SimpleMessageRoomResponse(
        Long id,
        SimpleMemberInfo fromMember,
        SimpleMemberInfo toMember,
        LocalDateTime createdTime,
        SimpleMessageResponse lastMessage,
        boolean isNewMessageInRoom
) {

    public static SimpleMessageRoomResponse from(MessageRoomWithNewMessageCheck messageRoomWithNewMessageCheck) {
        MemberDto fromMember = messageRoomWithNewMessageCheck.fromMember();
        MemberDto toMember = messageRoomWithNewMessageCheck.toMember();

        return new SimpleMessageRoomResponse(messageRoomWithNewMessageCheck.id(),
                new SimpleMemberInfo(fromMember.id(), fromMember.username(), fromMember.position(), fromMember.imgUrl()),
                new SimpleMemberInfo(toMember.id(), toMember.username(), toMember.position(), toMember.imgUrl()),
                messageRoomWithNewMessageCheck.createdTime(),
                SimpleMessageResponse.from(messageRoomWithNewMessageCheck.lastMessage()),
                messageRoomWithNewMessageCheck.isNewMessageInRoom());
    }
}
