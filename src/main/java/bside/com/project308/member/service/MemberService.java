package bside.com.project308.member.service;

import bside.com.project308.admin.Type;
import bside.com.project308.admin.service.UserLogService;
import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.DuplicatedMemberException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.SkillCategory;
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
import bside.com.project308.security.jwt.JwtTokenProvider;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final SkillMemberRepository skillMemberRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;
    private final CacheManager cacheManager;
    private final UserLogService userLogService;


    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
    }

    public Set<Member> getAllInterestingMemberByPosition(List<Position> interestingPositions, Member member){
        return memberRepository.findSetByPositionInAndIdNot(interestingPositions, member.getId());
    }

    public Set<Member> getTeckyMembers() {
        Set<String> teckyMemberId = new HashSet(Arrays.asList("2958207482", "2958207040", "2947153334", "2955591080"));
        return new HashSet<>(memberRepository.findInitialMemberProByUserProviderIdIn(teckyMemberId));
    }

    private Member getMemberWithInterest(Long memberId) {
        Member member = getMemberById(memberId);
        interestRepository.findByMember(member);
        return member;
    }

    public List<InterestDto> updateInterest(Long memberId, List<Position> positions) {
        Member member = getMemberWithInterest(memberId);
        member.getInterests().clear();
        //interestRepository.deleteAllInBatch(member.getInterests());
        List<Interest> interests = positions.stream().map(position -> Interest.of(position.name(), member)).toList();
        interestRepository.saveAll(interests);
        member.updateInterests(interests);
        return interests.stream().map(InterestDto::from).toList();
    }

    //todo: 쿼리dsl로 변경
    public MemberDto updateMemberInfo(Long memberId, MemberUpdateRequest memberUpdateRequest) {
        Member member = getMemberWithInterest(memberId);

        if (!CollectionUtils.isEmpty(memberUpdateRequest.skill())) {
            List<SkillMember> updatedSkills = deleteCurrentSkillMemberAndCreateNewSkillMember(memberUpdateRequest, member);
            List<SkillDto> skillDtos = updatedSkills.stream().map(SkillMember::getSkill)
                                                    .map(SkillDto::from)
                                                    .toList();
            member.updateMembeInfo(memberUpdateRequest.username(), memberUpdateRequest.intro(), updatedSkills);
            return MemberDto.from(member, skillDtos);
        }else{
            member.updateMembeInfo(memberUpdateRequest.username(), memberUpdateRequest.intro());
            return MemberDto.from(member);
        }
    }

    private List<SkillMember> deleteCurrentSkillMemberAndCreateNewSkillMember(MemberUpdateRequest memberUpdateRequest, Member member) {
        //Set<SkillMember> currentSkillSet = skillMemberRepository.findSetSkillByMember(member);
        //skillMemberRepository.deleteAllInBatch(currentSkillSet);
        List<Skill> skills = skillRepository.findBySkillNameIn(memberUpdateRequest.skill());
        List<SkillMember> updatedSkills = skills.stream()
                                                             .map(skill -> SkillMember.of(skill, member))
                                                             .toList();
        skillMemberRepository.saveAll(updatedSkills);
        return updatedSkills;
    }


    @Transactional(readOnly = true)
    public MemberDto getByUserProviderId(String userProviderId) {
        Member member = memberRepository.findByUserProviderId(userProviderId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        return MemberDto.from(member);
    }

    public MemberDto getByUserProviderIdAndUpdateLastAccessedTime(String userProviderId) {
        Member member = memberRepository.findByUserProviderId(userProviderId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        member.updateLastLoginTime();
        return MemberDto.from(member);
    }

    @Transactional
    public MemberDto getByUserProviderIdForLogin(String userProviderId) {
        Member member = memberRepository.findByUserProviderId(userProviderId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        member.updateLastLoginTime();
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

    public Member singUp(SignUpRequest signUpRequest) {
        memberRepository.findByUserProviderId(signUpRequest.userProviderId()).ifPresent(userProviderId -> {
            throw new DuplicatedMemberException(HttpStatus.BAD_REQUEST, ResponseCode.SIGN_UP_FAIL);
        });

        Member member = Member.builder().userProviderId(signUpRequest.userProviderId())
                                        .username(signUpRequest.username())
                                        .registrationSource(signUpRequest.registrationSource())
                                        .password(UUID.randomUUID().toString())
                                        .position(signUpRequest.position())
                                        .intro(signUpRequest.intro())
                                        .imgUrl(signUpRequest.imgUrl())
                                        .build();
        memberRepository.save(member);

        setMemberSkillAndInterest(member, signUpRequest);

        userLogService.saveUserLog(member.getId(), Type.SIGN_UP);

        return member;
    }


    private void setMemberSkillAndInterest(Member member, SignUpRequest signUpRequest) {

        List<Interest> interests = signUpRequest.interest().stream().map(interest -> Interest.of(interest, member)).toList();
        interestRepository.saveAll(interests);

        List<Skill> skills = skillRepository.findBySkillNameIn(signUpRequest.skill());
        List<SkillMember> skillMember = skills.stream().map(skill -> SkillMember.of(skill, member)).toList();
        skillMemberRepository.saveAll(skillMember);


        member.updateSkillAndInterests(interests, skillMember);
    }



    private Collection<SkillMember> updateSkill(MemberUpdateRequest memberUpdateRequest, Member member) {
        //todo: 입력값 검증할 것이므로 삭제 예정 코드
        if (CollectionUtils.isEmpty(memberUpdateRequest.skill())) {
            return skillMemberRepository.findSetSkillByMember(member);
        }
        Set<Skill> skillUpdate = skillRepository.findSetBySkillNameIn(memberUpdateRequest.skill());
        Set<SkillMember> skills = skillMemberRepository.findSetSkillByMember(member);
        Set<Skill> skillSet = skills.stream().map(SkillMember::getSkill).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(skillUpdate) && !skillSet.equals(skillUpdate)) {
            skillMemberRepository.deleteAllInBatch(skills);
            List<SkillMember> updatedSkillMember = skillUpdate.stream().map(skill -> SkillMember.of(skill, member)).toList();
            skillMemberRepository.saveAll(updatedSkillMember);
            return updatedSkillMember;
        }

        return skills;
    }

    public void delete(Long memberId) {
        String expiredToken = ((UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).token();
        //session기반 접속인 경우
        if(expiredToken != null){
            cacheManager.getCache(CacheConfig.CACHE_NAME_MATH_EXPIRED_TOKEN).put(expiredToken, JwtTokenProvider.TOKEN_EXPIRED);
        }

        Member member = getMember(memberId);
        userLogService.saveUserLog(member.getId(), Type.Withdrawal);
        memberRepository.delete(member);

    }


    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        return member;
    }
}
