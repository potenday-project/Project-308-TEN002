package bside.com.project308.match.algorithm;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.entity.TodayMatch;
import bside.com.project308.match.entity.Swipe;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.repository.SwipeRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class BasicMatchAlgorithm implements MatchAlgorithm {
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final MemberService memberService;
    private final CacheManager cacheManager;
    private final TodayMatchRepository todayMatchRepository;

    @Override
    //todo : member update기능 개발 시 cacheput도 설정해줘야 함
    //@Cacheable(value = "matchPartner", key = "#memberId")
    public MemberDto getMatchPartner(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        //사용자의 관심분야를 가져옴, 현재 관심분야는 Enum이 아닌 String으로 지정하여 향후 확장 가능성 있게 만들었음
        List<Interest> interests = interestRepository.findByMember(member);

        //현재 매칭은 position기반으로만 이뤄지기 때문에 interst를 position으로 변환하는 작업 필요
        List<Position> interestingPositions = interests.stream().map(interest -> Position.valueOf(interest.getInterest())).toList();

        //관심 position은 사람들 전부 가져옴, random함수도 생각했으나 native query를 사용해야 해서 현재는 이 방식
        Set<Member> allInterestingMember = memberRepository.findSetByPositionInAndIdNot(interestingPositions, memberId);

        Set<Member> swipedMembers = swipeRepository.findByFromMember(member).stream().map(Swipe::getToMember).collect(Collectors.toSet());
        Set<Member> matchedMembers = matchRepository.findByFromMemberOrToMember(member, member)
                                                    .stream()
                                                    .map(match -> match.getOtherMember(member))
                                                    .collect(Collectors.toSet());

        Set<MemberDto> allInterestingMemberDto = allInterestingMember.stream().map(MemberDto::from).collect(Collectors.toSet());
        Set<MemberDto> visitedMemberDtos = swipedMembers.stream().map(MemberDto::from).collect(Collectors.toSet());
        Set<MemberDto> matchedMemberDtos = matchedMembers.stream().map(MemberDto::from).collect(Collectors.toSet());

        if (matchedMemberDtos.containsAll(allInterestingMemberDto)) {
            log.info("getMatchPartner {}", "이상 접근");
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }


        //visitedMember와 matchedMember를 union하여 매칭 후보군에서 빼는 작업
        visitedMemberDtos.addAll(matchedMemberDtos);

        if (visitedMemberDtos.containsAll(allInterestingMemberDto)) {
            log.debug("모든 사용자를 방문하여 매치를 초기화합니다.");
            swipeRepository.deleteByFromMember(member);
            matchedMemberDtos = new HashSet<>();
        }

        allInterestingMemberDto.removeAll(matchedMemberDtos);

        Set<String> initialMemberId = new HashSet<String>(Arrays.asList("2958207482", "2958207040", "2947153334", "2955591080"));
        List<Member> initialMember = memberRepository.findInitialMemberProByUserProviderIdIn(initialMemberId);
        Set<Member> initialMemberset = new HashSet<>(initialMember);
        initialMemberset.retainAll(allInterestingMember);


        List<MemberDto> memberDtos = allInterestingMemberDto.stream().collect(Collectors.toList());

        //데이터가 많으면 굳이 전체 데이터를 shuffle할 필요는 없음
        if (memberDtos.size() >= 50) {
            memberDtos = memberDtos.subList(0, 50);
        }
        Collections.shuffle(memberDtos);
        if (CollectionUtils.isEmpty(memberDtos)) {
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }
        MemberDto targetMember = memberDtos.get(0);

/*
        Long latVisitedMemberId = visitedMemberCursorRepository.findByMember(member).map(VisitedMemberCursor::getId).orElse(1L);
        //랜덤으로 뽑게 되면 선택 안하고 다른 페이지 넘어가는 방식으로 계속 새로운 사용자를 찾을 수 있음
        //random 추천
        //한바튀 다 돈 경우 해결

        Set<Member> partnerCandidates = memberRepository.findTop50ByIdGreaterThanAndPositionInAndIdNot(latVisitedMemberId,
                                                                                                        positions,
                                                                                                        member.getId());
*/

        //todo: 한 바퀴 다 돈 경우에 대한 조치 필요
/*
        partnerCandidates.removeAll(swipedMembers);
        partnerCandidates.removeAll(collect);
        List<Member> resultCandidate = partnerCandidates.stream().collect(Collectors.toList());
        resultCandidate.sort(Comparator.comparing(Member::getId));
*/

/*        Member targetMember = resultCandidate.get(0);*/
        //lastVisitedCursor.setLastVisitedMemberId(targetMember.getId());
        return memberService.getMemberInfo(targetMember.id());
    }

    @Override
    @Cacheable(value = CacheConfig.CACHE_NAME_MATH_PARTNER, key = "#memberId")
    public LinkedList<MemberDto> getTodayMatchPartner(Long memberId) {

        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        List<TodayMatch> todayMatches = todayMatchRepository.findByFromMember(member);
        if(!CollectionUtils.isEmpty(todayMatches)){
            LinkedList<MemberDto> memberDtoStream = todayMatches.stream().map(todayMatch -> memberService.getMemberInfo(todayMatch.getToMember().getId())).collect(Collectors.toCollection(LinkedList::new));
            log.debug("today match list is {}", memberDtoStream);
            return memberDtoStream;
        }


        //사용자의 관심분야를 가져옴, 현재 관심분야는 Enum이 아닌 String으로 지정하여 향후 확장 가능성 있게 만들었음
        List<Interest> interests = interestRepository.findByMember(member);

        //현재 매칭은 position기반으로만 이뤄지기 때문에 interst를 position으로 변환하는 작업 필요
        List<Position> interestingPositions = interests.stream().map(interest -> Position.valueOf(interest.getInterest())).toList();

        //관심 position은 사람들 전부 가져옴, random함수도 생각했으나 native query를 사용해야 해서 현재는 이 방식
        Set<Member> allInterestingMember = memberRepository.findSetByPositionInAndIdNot(interestingPositions, memberId);
        Set<Member> visitedMembers = swipeRepository.findByFromMember(member).stream().map(Swipe::getToMember).collect(Collectors.toSet());
        Set<Member> matchedMembers = matchRepository.findByFromMemberOrToMember(member, member)
                                                    .stream()
                                                    .map(match -> match.getOtherMember(member))
                                                    .collect(Collectors.toSet());

        Set<String> teckyMemberId = new HashSet<String>(Arrays.asList("2958207482", "2958207040", "2947153334", "2955591080"));
        List<Member> teckyMember = memberRepository.findInitialMemberProByUserProviderIdIn(teckyMemberId);
        Set<Member> teckyMemberSet = new HashSet<>(teckyMember);


        allInterestingMember.removeAll(matchedMembers);

        Set<Member> copyA = new HashSet<>();
        copyA.addAll(visitedMembers);

        copyA.addAll(teckyMemberSet);

        if (allInterestingMember.isEmpty()) {
            log.error("매치 가능 사용자가 없습니다.");
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }


        if (copyA.containsAll(allInterestingMember)) {
            log.debug("모든 사용자를 방문하여 매치를 초기화합니다.");
            swipeRepository.deleteByFromMember(member);
            visitedMembers = new HashSet<>();
        }

        allInterestingMember.removeAll(visitedMembers);

        teckyMemberSet.retainAll(allInterestingMember);


        allInterestingMember.removeAll(teckyMemberSet);


        List<Member> memberWithoutTecky = allInterestingMember.stream().collect(Collectors.toList());


        //데이터가 많으면 굳이 전체 데이터를 shuffle할 필요는 없음
        if (memberWithoutTecky.size() >= 50) {
            memberWithoutTecky = memberWithoutTecky.subList(0, 50);
        }

        teckyMember = new ArrayList<>(teckyMemberSet);


        Collections.shuffle(memberWithoutTecky);
        if (CollectionUtils.isEmpty(memberWithoutTecky)) {
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }

        teckyMember.addAll(memberWithoutTecky);

        List<Member> finalMemberList = teckyMember;

        if (finalMemberList.size() >= 10) {
            finalMemberList = finalMemberList.subList(0, 5);
        }

        todayMatches = finalMemberList.stream().map(toMember -> TodayMatch.of(member, toMember)).toList();
        todayMatchRepository.saveAll(todayMatches);

        //todo: 쿼리 성능개선 필요 N+1

        LinkedList<MemberDto> todayList = finalMemberList.stream()
                  .map(matchTarget-> memberService.getMemberInfo(matchTarget.getId()))
                  .collect(Collectors.toCollection(LinkedList::new));

        return todayList;
    }
}
