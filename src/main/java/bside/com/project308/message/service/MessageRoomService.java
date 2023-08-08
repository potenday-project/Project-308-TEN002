package bside.com.project308.message.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;

    public void createMessageRoom(Member fromMember, Member toMember) {
        MessageRoom newMessageRoom = MessageRoom.of(fromMember, toMember);
        messageRoomRepository.save(newMessageRoom);
    }

    public MessageRoomDto getMessageRoom(Long fromMemberId, Long toMemberId) {

        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        MessageRoom messageRoom = messageRoomRepository.findByFromMemberAndToMember(fromMember, toMember).orElseThrow(
                () -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM)
        );

        return MessageRoomDto.from(messageRoom);
    }


}
