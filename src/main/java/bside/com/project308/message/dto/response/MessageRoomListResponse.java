package bside.com.project308.message.dto.response;

import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.SimpleMemberInfo;
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;

import java.time.LocalDateTime;
import java.util.List;

public record MessageRoomListResponse(Long loginMemberId,
                                      List<SimpleMessageRoomResponse> messageRooms) {

    public static MessageRoomListResponse from(Long loginMemberId, List<SimpleMessageRoomResponse> simpleMessageRoomResponses) {
        return new MessageRoomListResponse(loginMemberId, simpleMessageRoomResponses);
    }


}
