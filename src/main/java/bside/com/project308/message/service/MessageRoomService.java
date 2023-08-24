package bside.com.project308.message.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.response.MessageReadResponse;
import bside.com.project308.message.dto.response.MessageResponse;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRoomRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageRoomService {

    private final MessageRoomRepository messageRoomRepository;
    @Transactional(readOnly = true)
    public MessageRoom getMessageRoom(Member fromMember, Member toMember) {
        return messageRoomRepository.findByMemberSet(fromMember, toMember)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM));
    }

    @Transactional(readOnly = true)
    public MessageRoom getMessageRoom(Long messageRoomId) {
        return messageRoomRepository.findById(messageRoomId)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM));
    }

    @Transactional(readOnly = true)
    public MessageRoom getMessageRoomByMatch(Long matchId) {
        return messageRoomRepository.findById(matchId)
                                    .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.NO_MESSAGE_ROOM));
    }


    @Transactional(readOnly = true)
    public List<MessageRoom> getAllMessageRoomList(Member member) {
        List<MessageRoom> messageRooms = messageRoomRepository.findByFromMemberOrToMember(member, member);
        return messageRooms;
    }


    public MessageRoom createMessageRoom(Member fromMember, Member toMember, Match createdMatch) {
        MessageRoom newMessageRoom = MessageRoom.of(fromMember, toMember, createdMatch);
        messageRoomRepository.save(newMessageRoom);
        return newMessageRoom;
    }


}
