package bside.com.project308.match.event;

import bside.com.project308.match.controller.usecase.MakeMatchAndMessageRoom;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.message.dto.MessageRoomDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class MatchEvent extends ApplicationEvent {

    private final MatchDto match;
    private final MessageRoomDto messageRoom;
    public MatchEvent(MakeMatchAndMessageRoom source, MatchDto newMatch, MessageRoomDto messageRoom) {
        super(source);
        this.match = newMatch;
        this.messageRoom = messageRoom;
    }


}
