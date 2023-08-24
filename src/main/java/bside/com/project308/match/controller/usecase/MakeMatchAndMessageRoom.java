package bside.com.project308.match.controller.usecase;

import bside.com.project308.match.event.MatchEvent;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MakeMatchAndMessageRoom {
    private final MatchService matchService;
    private final MessageRoomService messageRoomService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MessageRoomDto execute(Member fromMember, Member toMember) {
        Match newMatch = matchService.createMatch(fromMember, toMember);
        MessageRoom newMessageRoom = messageRoomService.createMessageRoom(fromMember, toMember, newMatch);
        MessageRoomDto messageRoomDto = MessageRoomDto.from(newMessageRoom);

        notifyMatch(messageRoomDto.matchDto(), messageRoomDto);
        return messageRoomDto;
    }

    @Transactional
    public MessageRoom executeForSingUp(Member fromMember, Member toMember) {
        Match newMatch = matchService.createMatch(fromMember, toMember);
        MessageRoom newMessageRoom = messageRoomService.createMessageRoom(fromMember, toMember, newMatch);
        return newMessageRoom;
    }

    private void notifyMatch(MatchDto matchDto, MessageRoomDto messageRoomDto) {
        eventPublisher.publishEvent(new MatchEvent(this, matchDto, messageRoomDto));
    }
}
