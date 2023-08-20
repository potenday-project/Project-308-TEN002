package bside.com.project308.member.contoller.usecase;

import bside.com.project308.match.controller.usecase.MakeMatchAndMessageRoom;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.dto.InterestDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.SkillDto;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.controller.usecase.WriteMessageAndUpdateLastMessage;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.entity.MessageRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class SingUpMemberAndSetDefaultMatch {

    private final MemberService memberService;
    private final SwipeService swipeService;
    private final MakeMatchAndMessageRoom makeMatchAndMessageRoom;
    private final WriteMessageAndUpdateLastMessage writeMessageAndUpdateLastMessage;


    @Transactional
    public MemberDto execute(SignUpRequest signUpRequest) {
        Member createdMember = memberService.singUp(signUpRequest);
        //tecky 팀계정과 자동 매칭
        initialMatchWithTecky(createdMember);
        //tecky 초기 멤버들과 자동 매칭
        initialSwipe(createdMember);
        return MemberDto.from(createdMember,
                createdMember.getInterests().stream().map(InterestDto::from).toList(),
                createdMember.getSkills().stream().map(skillMember -> SkillDto.from(skillMember.getSkill())).toList());

    }
    public void initialMatchWithTecky(Member createdMember) {
        Member tecky = memberService.getMemberById(1L);
        MessageRoom newMessageRoom = makeMatchAndMessageRoom.executeForSingUp(tecky, createdMember);
        writeDefaultTeckyMessage(tecky,createdMember, newMessageRoom);
    }

    private void writeDefaultTeckyMessage(Member tecky, Member createdMember, MessageRoom messageRoom) {
        String message = "만나서 반가워요! :) 저희는 창업 팀매칭을 지원하는 tecky팀입니다!";
        MessageRequest messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);

        message = "나와 핏이 맞을 것 같은 사람을 찾으셨다면 오른쪽으로 스와이프 해보세요";
        messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);

        message = "새로운 만남이 성사될 거에요!";
        messageRequest = new MessageRequest(messageRoom.getId(), message);
        writeMessageAndUpdateLastMessage.execute(tecky.getId(), messageRequest);
    }

    public void initialSwipe(Member createdMember) {

        Set<String> initialMemberId = new HashSet(Arrays.asList("2958207482", "2958207040", "2947153334", "2955591080"));
        if(!initialMemberId.contains(createdMember.getUserProviderId())){
            Set<Member> teckyMembers = memberService.getTeckyMembers();
            teckyMembers.stream().forEach(member -> swipeService.swipe(member, createdMember, true));
        }

    }



}
