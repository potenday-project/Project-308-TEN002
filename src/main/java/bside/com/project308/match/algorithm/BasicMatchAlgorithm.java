package bside.com.project308.match.algorithm;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.MemberGrade;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.entity.TodayMatch;
import bside.com.project308.match.entity.Swipe;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.repository.SwipeRepository;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final MemberService memberService;

    @Override
    public boolean support(MemberGrade memberGrade) {
        return memberGrade == MemberGrade.INITIAL_MEMBER;
    }

    @Override
    public List<Member> getTodayMatchPartner(Member member) {

        //현재 매칭은 position기반으로만 이뤄지기 때문에 interst를 position으로 변환하는 작업 필요
        List<Position> interestingPositions = member.getInterests().stream().map(interest -> Position.valueOf(interest.getInterest())).toList();
        //관심 position은 사람들 전부 가져옴, random함수도 생각했으나 native query를 사용해야 해서 현재는 이 방식
        Set<Member> allInterestingMember = memberService.getAllInterestingMemberByPosition(interestingPositions, member);
        //관심직군 모든 사람과 match되어 match가능한 사람이 없으면 예외 발생
        matchAvailableCheck(member, allInterestingMember);

        Set<Member> visitedMembers = swipeRepository.findByFromMember(member).stream().map(Swipe::getToMember).collect(Collectors.toSet());

        //tecky member중 관심직군 멤버만 추출
        Set<Member> teckyMember = memberService.getTeckyMembers();
        teckyMember.retainAll(allInterestingMember);
        allInterestingMember.removeAll(teckyMember);


        List<Member> allInterestingMembersWithoutTecky = removeVisitedMember(member, allInterestingMember, visitedMembers);


        List<Member> randomMemberWithoutTecky = shuffleMemberWithoutTecky(allInterestingMembersWithoutTecky);

        List<Member> finalMemberList = new ArrayList<>(teckyMember);
        finalMemberList.addAll(randomMemberWithoutTecky);

        if (finalMemberList.size() >= 5) {
            finalMemberList = finalMemberList.subList(0, 5);
        }


        return finalMemberList;
    }

    private List<Member> removeVisitedMember(Member member, Set<Member> allInterestingMember, Set<Member> visitedMembers) {
        if (visitedMembers.containsAll(allInterestingMember)) {
            log.debug("모든 사용자를 방문하여 매치를 초기화합니다.");
            swipeRepository.deleteByFromMember(member);
            visitedMembers = new HashSet<>();
        }
        allInterestingMember.removeAll(visitedMembers);
        return allInterestingMember.stream().collect(Collectors.toList());
    }

    private static List<Member> shuffleMemberWithoutTecky(List<Member> memberWithoutTecky) {
        //데이터가 많으면 굳이 전체 데이터를 shuffle할 필요는 없음
        if (memberWithoutTecky.size() >= 50) {
            memberWithoutTecky = memberWithoutTecky.subList(0, 50);
        }
        if (CollectionUtils.isEmpty(memberWithoutTecky)) {
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }
        Collections.shuffle(memberWithoutTecky);
        return memberWithoutTecky;
    }

    private void matchAvailableCheck(Member member, Set<Member> allInterestingMember) {
        Set<Member> matchedMembers = matchRepository.findByFromMemberOrToMember(member, member)
                                                    .stream()
                                                    .map(match -> match.getOtherMember(member))
                                                    .collect(Collectors.toSet());


        allInterestingMember.removeAll(matchedMembers);
        if (allInterestingMember.isEmpty()) {
            log.debug("매치 가능 사용자가 없습니다.");
            throw new ResourceNotFoundException(ResponseCode.NO_MORE_PARTNER, ResponseCode.NO_MORE_PARTNER.getDesc());
        }
    }


}
