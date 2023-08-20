package bside.com.project308.message.controller.usecase;

import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class GetMessageRoom {

    private final MemberService memberService;
    private final MessageRoomService messageRoomService;

    @Transactional(readOnly = true)
    public MessageRoomDto execute(Long fromMemberId, Long toMemberId) {
        Member fromMember = memberService.getMemberById(fromMemberId);
        Member toMember = memberService.getMemberById(toMemberId);

        MessageRoom messageRoom = messageRoomService.getMessageRoom(fromMember, toMember);
        return MessageRoomDto.from(messageRoom);
    }
}
