package bside.com.project308.message.controller.usecase;

import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.response.MessageReadResponse;
import bside.com.project308.message.dto.response.MessageResponse;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReadAllMessageAndGetMessageRoom {
    private final MessageRoomService messageRoomService;
    private final MessageService messageService;
    private final MatchService matchService;
    @Transactional
    public MessageReadResponse execute(Long messageRoomId, Long memberId) {
        MessageRoom messageRoom = messageRoomService.getMessageRoom(messageRoomId);
        Match match = matchService.getMatch(messageRoom.getFromMember(), messageRoom.getToMember());

        List<Message> messages = messageService.readMessage(messageRoom, memberId, null);
        List<MessageResponse> messageResponses = messages.stream().map(MessageDto::from).map(MessageResponse::from).toList();
        Member partner = match.getFromMember().getId() == memberId ? match.getToMember() : match.getFromMember();

        return new MessageReadResponse(memberId, partner.getUsername(), partner.getId(), match.getId(), messageResponses);
    }
}
