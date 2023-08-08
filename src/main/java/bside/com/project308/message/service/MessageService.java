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

@Transactional
@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageRoomRepository messageRoomRepository;

    public MessageDto writeMessage(Long messageRoomId, Long memberId, String content) {
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
        return MessageDto.from(newMessage);
    }

    public void readMessage(Long chatRoomId, Long fromMemberId, Pageable pageable) {

    }
}
