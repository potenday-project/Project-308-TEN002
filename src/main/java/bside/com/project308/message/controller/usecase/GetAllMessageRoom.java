package bside.com.project308.message.controller.usecase;

import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.dto.MessageDto;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GetAllMessageRoom {
    private final MemberService memberService;
    private final MessageRoomService messageRoomService;
    private final MessageService messageService;

    @Transactional(readOnly = true)
    public List<MessageRoomWithNewMessageCheck> execute(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        List<MessageRoom> allMessageRoomList = messageRoomService.getAllMessageRoomList(member);
        List<MessageRoomDto> messageRoomDtos = allMessageRoomList.stream()
                                                         .map(messageRoom -> MessageRoomDto.from(messageRoom, member)).collect(Collectors.toList());

        return allMessageRoomList.stream()
                                 .map(messageRoom -> setDefaultLastMessage(messageRoom, member))
                                 .sorted(Comparator.comparing(messageRoom ->((MessageRoomWithNewMessageCheck) messageRoom)
                                                                                                                             .lastMessage()
                                                                                                                             .messageCreatedTime())
                                                   .reversed())
                                .toList();



    }

    private MessageRoomWithNewMessageCheck setDefaultLastMessage(MessageRoom messageRoom, Member member) {
        if (messageRoom.getLastMessage() == null) {
            MessageDto messageDto = member == messageRoom.getFromMember() ?
                    MessageDto.defaultMessage(messageRoom.getId(), messageRoom.getToMember()) :
                    MessageDto.defaultMessage(messageRoom.getId(), messageRoom.getFromMember());
            return MessageRoomWithNewMessageCheck.from(member.getId(), messageRoom, messageDto, false);
        }
        boolean newMessageCheckResult = messageService.isUnReadMessageInRoom(member, messageRoom);
        return MessageRoomWithNewMessageCheck.from(member.getId(), messageRoom, newMessageCheckResult);

    }
}
