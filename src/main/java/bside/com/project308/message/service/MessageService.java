package bside.com.project308.message.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRepository;
import bside.com.project308.message.repository.MessageRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    @Transactional(readOnly = true)
    public boolean isUnReadMessageInRoom(Member member, MessageRoom messageRoom){
        boolean messageCheckResult = member == messageRoom.getFromMember() ?
                messageRepository.existsByIsFromMemberMessageAndIsReadFalseAndMessageRoom(false, messageRoom) :
                messageRepository.existsByIsFromMemberMessageAndIsReadFalseAndMessageRoom(true, messageRoom);
        return messageCheckResult;
    }


    public Message writeMessage(MessageRoom messageRoom, Long memberId, String content) {

        Message newMessage = null;

        if (messageRoom.getFromMember().getId() == memberId) {
            newMessage = Message.of(content, messageRoom, true);
        } else if(messageRoom.getToMember().getId() == memberId){
            newMessage = Message.of(content, messageRoom, false);
        } else{
            throw new UnAuthorizedAccessException(ResponseCode.NOT_AUTHORIZED_ACCESS_TO_MESSAGING);
        }

        messageRepository.save(newMessage);
        return newMessage;
    }

    public List<Message> readMessage(MessageRoom messageRoom, Long memberId, Pageable pageable) {

        List<Message> messages = messageRepository.findByMessageRoom(messageRoom);

        //로그인 사용자 기준 상대방이 보낸 메시지에 대해 읽기처리
        if (messageRoom.getFromMember().getId() == memberId) {
            messages.stream().filter(message -> !message.isFromMemberMessage()).forEach(Message::readMessage);
        }else{
            messages.stream().filter(Message::isFromMemberMessage).forEach(Message::readMessage);
        }

        return messages;
    }
}
