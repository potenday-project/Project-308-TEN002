package bside.com.project308.message.dto;

import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;

import java.time.LocalDateTime;

public record MessageRoomDto(Long id,
                             MemberDto fromMember,
                             MemberDto toMember,
                             LocalDateTime createdTime,
                             MessageDto lastMessage,
                                MatchDto matchDto


) {

    public static MessageRoomDto from(MessageRoom messageRoom) {
        MessageDto messageDto = null;
        //todo : lastMessage null에 대한 해결 필요
        if (messageRoom.getLastMessage() == null) {
            messageDto = MessageDto.defaultMessage(messageRoom.getId(), messageRoom.getToMember());
        }else{
            messageDto = MessageDto.from(messageRoom.getLastMessage());
        }
        return new MessageRoomDto(messageRoom.getId(),
                                    MemberDto.from(messageRoom.getFromMember()),
                                    MemberDto.from(messageRoom.getToMember()),
                                    messageRoom.getCreatedTime(),
                                    messageDto,
                                    MatchDto.from(messageRoom.getMatch()));
    }


}
