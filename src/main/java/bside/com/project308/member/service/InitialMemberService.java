package bside.com.project308.member.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.controller.usecase.MakeMatchAndMessageRoom;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.message.controller.usecase.WriteMessageAndUpdateLastMessage;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRoomRepository;
import bside.com.project308.message.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class InitialMemberService {

    private final MemberRepository memberRepository;

    private final MemberService memberService;
    private final MessageRoomService messageRoomService;

    private final SwipeService swipeService;
    private final MakeMatchAndMessageRoom makeMatchAndMessageRoom;
    private final WriteMessageAndUpdateLastMessage writeMessageAndUpdateLastMessage;

    public MemberDto singUp(SignUpRequest signUpRequest) {
        MemberDto memberDto = memberService.singUp(signUpRequest);
        Member createdMember = memberRepository.findById(memberDto.id()).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        initialMatch(createdMember);



        //todo: 양방향 연관관계 및 fetch join에 대해서는 고민, 데이터 뻥튀기 문제 있음
        return memberDto;
    }

    public List<MatchDto> initialMatch(Member createdMember) {
        MatchDto match = defaultTeckyMessage(createdMember);
        Set<String> initialMemberId = new HashSet<String>(Arrays.asList("2958207482", "2958207040", "2947153334", "2955591080"));
        if(!initialMemberId.contains(createdMember.getUserProviderId())){
            List<Member> initialMember = memberRepository.findInitialMemberProByUserProviderIdIn(initialMemberId);
            initialMember.stream().forEach(member -> swipeService.swipe(member, createdMember, true));
        }


      /*  List<Member> initialMember = memberRepository.findInitialMemberProByUserProviderIdIn(List.of("2958207040", "1"));

        List<MatchDto> matchDtos = initialMember.stream()
                                                   .map(member -> matchService.createMatch(member, createdMember)).toList();

        List<MessageRoomDto> messageRoomDtos = initialMember.stream().map(member -> messageRoomService.getMessageRoom(member.getId(), createdMember.getId())).toList();
        for (int i = 0; i < initialMember.size(); i++) {
            Long loginMemberId = initialMember.get(i).getId();
            Long messageRoomId = messageRoomDtos.get(i).id();
            String position = "";
            switch(initialMember.get(i).getPosition()){
                case PM_PO -> position = "기획자 ";
                case BACK_END -> position = "BE 엔지니어 ";
                case FRONT_END -> position = "FE 엔지니어 ";
                case DESIGNER -> position = "디자이너 ";
            }
            String message = "안녕하세요~ 저는 " + position + initialMember.get(i).getUsername() + "입니다.";
            MessageRequest messageRequest = new MessageRequest(messageRoomId, message);
            messageRoomService.writeMessage(loginMemberId, messageRequest);
        }

        for (int i = 0; i < initialMember.size(); i++) {
            Long loginMemberId = initialMember.get(i).getId();
            Long messageRoomId = messageRoomDtos.get(i).id();
            String message = "만나서 반가워요! :) 저는 테키 서비스를 만들고 있고 사람들을 연결해드리는 일을 하고 있어요.";
            MessageRequest messageRequest = new MessageRequest(messageRoomId, message);
            messageRoomService.writeMessage(loginMemberId, messageRequest);
        }*/

        return List.of(match);
    }

    private MatchDto defaultTeckyMessage(Member createdMember) {
        Member tecky = memberRepository.findByUserProviderId("1").orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        MessageRoomDto messageRoomDto = makeMatchAndMessageRoom.execute(tecky, createdMember);

        //Match match = matchService.createMatch(tecky, createdMember);

        MessageRoom messageRoom = messageRoomService.getMessageRoom(tecky, createdMember);
        String message = "만나서 반가워요! :) 저희는 창업 팀매칭을 지원하는 tecky팀입니다!";
        MessageRequest messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);

        message = "나와 핏이 맞을 것 같은 사람을 찾으셨다면 오른쪽으로 스와이프 해보세요";
        messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);

        message = "새로운 만남이 성사될 거에요!";
        messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);
        return messageRoomDto.matchDto();
    }


}
