package bside.com.project308.member.service;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.DuplicatedMemberException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
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
import bside.com.project308.security.jwt.JwtTokenProvider;
import bside.com.project308.security.security.UserPrincipal;
import jakarta.annotation.PostConstruct;
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



    @Transactional(readOnly = true)
    public MemberDto getByUserProviderId(String userProviderId) {
        Member member = memberRepository.findByUserProviderId(userProviderId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
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

    public MemberDto singUp(SignUpRequest signUpRequest) {
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

        member.updateMember(
                memberUpdateRequest.username(),
                Position.valueOf(memberUpdateRequest.position()),
                memberUpdateRequest.intro(),
                memberUpdateRequest.imgUrl());

        Collection<Skill> skills = updateSkill(memberUpdateRequest, member).stream().map(SkillMember::getSkill).toList();
        Collection<Interest> interests = updateInterest(memberUpdateRequest, member);


        return MemberDto.from(member,
                interests.stream().map(InterestDto::from).toList(),
                skills.stream().map(SkillDto::from).toList());
    }

    private Collection<Interest> updateInterest(MemberUpdateRequest memberUpdateRequest, Member member) {
        //todo: 입력값 검증할 것이므로 삭제 예정 코드
        if (CollectionUtils.isEmpty(memberUpdateRequest.interest())) {
           return interestRepository.findSetByMember(member);
        }
        Set<String> interestUpdate = new HashSet(memberUpdateRequest.interest());

        //Set<Interest> interestUpdate = memberUpdateRequest.interest().stream().map(interest -> Interest.of(interest, member)).collect(Collectors.toSet());
        Set<Interest> interestSet = interestRepository.findSetByMember(member);
        Set<String> interests = interestSet.stream().map(Interest::getInterest).collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(interestUpdate) && !interests.equals(interestUpdate)) {

            interestRepository.deleteAllInBatch(interestSet);
            List<Interest> updateResult = interestUpdate.stream().map(interest -> Interest.of(interest, member)).toList();
            interestRepository.saveAll(updateResult);
            return updateResult;
        }

        return interestSet;
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
        cacheManager.getCache(CacheConfig.CACHE_NAME_MATH_EXPIRED_TOKEN).put(expiredToken, JwtTokenProvider.TOKEN_EXPIRED);
        Member member = getMember(memberId);
        memberRepository.delete(member);
    }


    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        return member;
    }
}
