package bside.com.project308.match.controller.usecase;

import bside.com.project308.match.entity.Match;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.member.entity.Member;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MakeMatchAndMessageRoom {
    private final MatchService matchService;
    private final MessageRoomService messageRoomService;

    @Transactional
    public MessageRoomDto execute(Member fromMember, Member toMember) {
        Match newMatch = matchService.createMatch(fromMember, toMember);
        MessageRoom newMessageRoom = messageRoomService.createMessageRoom(fromMember, toMember, newMatch);
        return MessageRoomDto.from(newMessageRoom);
    }
}
