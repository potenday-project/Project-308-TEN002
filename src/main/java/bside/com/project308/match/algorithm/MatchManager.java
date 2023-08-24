package bside.com.project308.match.algorithm;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.MemberGrade;
import bside.com.project308.match.entity.TodayMatch;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Slf4j
public class MatchManager {

    private final List<MatchAlgorithm> algorithms;
    private final TodayMatchRepository todayMatchRepository;
    private final MemberService memberService;
    @Cacheable(value = CacheConfig.CACHE_NAME_MATH_PARTNER, key = "#member.id")
    public List<MemberDto> getMatchPartner(Member member, MemberGrade grade) {

        List<TodayMatch> todayMatches = todayMatchRepository.findByFromMember(member);
        if(!CollectionUtils.isEmpty(todayMatches)){
            LinkedList<MemberDto> memberDtoStream = todayMatches.stream().map(todayMatch -> memberService.getMemberInfo(todayMatch.getToMember().getId())).collect(Collectors.toCollection(LinkedList::new));
            log.debug("today match list is {}", memberDtoStream);
            return memberDtoStream;
        }


        for (MatchAlgorithm algorithm : algorithms) {
            if(algorithm.support(grade)){
                List<Member> todayMatchPartners = algorithm.getTodayMatchPartner(member);


                todayMatchPartners.stream().map(toMember -> TodayMatch.of(member, toMember)).toList();


                todayMatchRepository.saveAll(todayMatches);
                //todo: 쿼리 성능개선 필요 N+1
                return todayMatchPartners.stream().map(matchTarget-> memberService.getMemberInfo(matchTarget.getId()))
                                                                    .collect(Collectors.toCollection(LinkedList::new));
            }
        }
        return Collections.emptyList();
    }

}
