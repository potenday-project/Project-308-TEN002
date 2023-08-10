package bside.com.project308.message.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
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
    private final MessageRoomRepository messageRoomRepository;

    //todo:어차피 영속성컨텍스트로 묶이니 안전하게 dto로 반환해서 MessageRoomService에서의 find호출은 추가 쿼리를 안 날리 -> 안전하게 dto로 반환할지 고민
    public Message writeMessage(Long messageRoomId, Long memberId, String content) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId).orElseThrow(() ->
                new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM)
        );
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

    public List<Message> readMessage(Long messageRoomId, Long memberId, Pageable pageable) {
        MessageRoom messageRoom = messageRoomRepository.findById(messageRoomId).orElseThrow(() ->
                new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM)
        );
        List<Message> messages = messageRepository.findByMessageRoom(messageRoom);
        //메시지 읽기 처리 todo: 리팩토링 필요
        if (messageRoom.getFromMember().getId() == memberId) {
            messages.stream().filter(message -> !message.isFromMemberMessage()).forEach(Message::readMessage);
        }else{
            messages.stream().filter(Message::isFromMemberMessage).forEach(Message::readMessage);
        }

        return messages;
    }
}
