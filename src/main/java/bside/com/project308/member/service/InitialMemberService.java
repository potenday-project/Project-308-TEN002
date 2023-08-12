package bside.com.project308.member.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.DuplicatedMemberException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.member.dto.InterestDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.SkillDto;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.entity.SkillMember;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import bside.com.project308.message.repository.MessageRoomRepository;
import bside.com.project308.message.service.MessageRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class InitialMemberService {

    private final MemberRepository memberRepository;
    private final MatchService matchService;
    private final MemberService memberService;
    private final MessageRoomService messageRoomService;
    private final MessageRoomRepository messageRoomRepository;


    public MemberDto singUp(SignUpRequest signUpRequest) {
        MemberDto memberDto = memberService.singUp(signUpRequest);
        Member createdMember = memberRepository.findById(memberDto.id()).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        initialMatch(createdMember);



        //todo: 양방향 연관관계 및 fetch join에 대해서는 고민, 데이터 뻥튀기 문제 있음
        return memberDto;
    }

    public List<MatchDto> initialMatch(Member createdMember) {
        List<Member> initialMember = memberRepository.findInitialMemberProByUserProviderIdIn(List.of("2958207040", "1"));
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
        }

        return matchDtos;
    }


}
