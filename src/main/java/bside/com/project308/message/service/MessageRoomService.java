package bside.com.project308.message.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.dto.response.MessageRoomListResponse;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRepository;
import bside.com.project308.message.repository.MessageRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    private final MemberRepository memberRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;

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

    @Transactional(readOnly = true)
    public List<MessageRoomWithNewMessageCheck> getAllMessageRoomList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<MessageRoom> messageRooms = messageRoomRepository.findByFromMemberOrToMember(member, member);


        return messageRooms.stream()
                            .map(messageRoom -> {
                                if(messageRoom.getLastMessage() == null){
                                    MessageDto messageDto = memberId == messageRoom.getFromMember().getId() ?
                                            MessageDto.defaultMessage(messageRoom.getId(), messageRoom.getToMember().getId()) :
                                            MessageDto.defaultMessage(messageRoom.getId(), messageRoom.getFromMember().getId());
                                    return MessageRoomWithNewMessageCheck.from(memberId, messageRoom, messageDto, false);
                                }

                               boolean newMessageCheckResult = memberId == messageRoom.getFromMember().getId() ?
                                                       messageRepository.existsByIsFromMemberMessageAndIsReadFalseAndMessageRoom(false, messageRoom) :
                                                       messageRepository.existsByIsFromMemberMessageAndIsReadFalseAndMessageRoom(true, messageRoom);
                               return MessageRoomWithNewMessageCheck.from(memberId, messageRoom, newMessageCheckResult);
                           })
                           .sorted(Comparator.comparing(messageRoom ->((MessageRoomWithNewMessageCheck) messageRoom).lastMessage().messageCreatedTime()).reversed())
                           .toList();
    }


    public void writeMessage(Long memberId, MessageRequest messageRequest) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRequest.messageRoomId()).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM));
        Message newMessage = messageService.writeMessage(messageRequest.messageRoomId(), memberId, messageRequest.content());
        messageRoom.updateLastMessage(newMessage);
    }

    public List<MessageDto> readAllMessageInRoom(Long memberId, Long messageRoomId, Pageable pageable) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM));
        List<Message> messages = messageService.readMessage(messageRoomId, memberId, pageable);
        return messages.stream().map(MessageDto::from).collect(Collectors.toList());
    }
}
