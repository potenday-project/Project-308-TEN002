package bside.com.project308.message.dto;

import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;

import java.time.LocalDateTime;

public record MessageRoomDto(Long id,
                             MemberDto fromMember,
                             MemberDto toMember,
                             LocalDateTime createdTime
) {

    public static MessageRoomDto from(MessageRoom messageRoom) {
        return new MessageRoomDto(messageRoom.getId(), MemberDto.from(messageRoom.getFromMember()), MemberDto.from(messageRoom.getToMember()), messageRoom.getCreatedTime());
    }
}
