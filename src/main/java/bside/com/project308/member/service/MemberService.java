package bside.com.project308.member.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.dto.InterestDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.SkillDto;
import bside.com.project308.member.dto.request.MemberUpdateRequest;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.entity.SkillMember;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.repository.SkillMemberRepository;
import bside.com.project308.member.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final SkillMemberRepository skillMemberRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;

    @Transactional(readOnly = true)
    public MemberDto getByUserProviderId(String userProviderId) {
        Member member = memberRepository.findByUserProviderId(userProviderId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        return MemberDto.from(member);
    }


    @Transactional(readOnly = true)
    public MemberDto getMemberInfo(Long memberId) {
        Member member = getMember(memberId);
        List<InterestDto> interestDtos = interestRepository.findByMember(member).stream().map(InterestDto::from).toList();
        List<SkillMember> skillByMember = skillMemberRepository.findSkillByMember(member);
        List<SkillDto> skillDtos = skillByMember.stream().map(SkillMember::getSkill).map(SkillDto::from).toList();

        return MemberDto.from(member, interestDtos, skillDtos);
    }

    public MemberDto singUp(SignUpRequest signUpRequest, MemberDto memberDto) {
        Member member = Member.builder().userProviderId(memberDto.userProviderId())
                                        .username(memberDto.username())
                                        .registrationSource(memberDto.registrationSource())
                                        .password(UUID.randomUUID().toString())
                                        .position(signUpRequest.position())
                                        .intro(signUpRequest.intro())
                                        .imgUrl(memberDto.imgUrl())
                                        .build();
        memberRepository.save(member);

        List<Skill> skills = skillRepository.findBySkillNameIn(signUpRequest.skill());
        List<SkillMember> skillMember = skills.stream().map(skill -> SkillMember.of(skill, member)).toList();
        skillMemberRepository.saveAll(skillMember);


        List<Interest> interests = signUpRequest.interest().stream().map(interest -> Interest.of(interest, member)).toList();
        interestRepository.saveAll(interests);

        //todo: 양방향 연관관계 및 fetch join에 대해서는 고민, 데이터 뻥튀기 문제 있음
        return MemberDto.from(member,
                interests.stream().map(InterestDto::from).toList(),
                skills.stream().map(SkillDto::from).toList());
    }

    public MemberDto update(Long memberId, MemberUpdateRequest memberUpdateRequest) {
        Member member = getMember(memberId);
        member.updateMember(memberUpdateRequest.getUsername(), memberUpdateRequest.getPosition());
        return MemberDto.from(member);
    }

    public void delete(Long memberId) {
        Member member = getMember(memberId);
        memberRepository.delete(member);
    }


    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        return member;
    }
}
