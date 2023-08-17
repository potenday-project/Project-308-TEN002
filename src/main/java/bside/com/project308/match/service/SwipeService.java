package bside.com.project308.match.service;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Swipe;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.repository.SwipeRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final CacheManager cacheManager;


    public boolean swipe(Member fromMember, Member toMember, boolean like){
        //방문 사용자를 매치 대상자 캐시에서 제외
        evictSwipedMember(fromMember.getId(), MemberDto.from(toMember));

        //만약 like기록이 과거에 있던 경우 update
        Swipe visit = swipeRepository.findByFromMemberAndToMember(fromMember, toMember).orElseGet(() -> Swipe.of(fromMember, toMember, like));
        visit.updateLike(like);
        swipeRepository.save(visit);

        if (like) {
            Optional<Swipe> toMemberVisit = swipeRepository.findByFromMemberAndToMember(toMember, fromMember);
            if(toMemberVisit.isPresent() && toMemberVisit.get().getIsLike()){
                swipeRepository.delete(toMemberVisit.get());
                swipeRepository.delete(visit);
                return true;
            }
        }

        return false;
    }

    private void evictSwipedMember(Long fromMemberId, MemberDto memberDto) {
        Cache matchPartnerCache = cacheManager.getCache(CacheConfig.CACHE_NAME_MATH_PARTNER);
        List<MemberDto> matchPartners = matchPartnerCache.get(fromMemberId, LinkedList.class);

        if (!CollectionUtils.isEmpty(matchPartners)) {
            matchPartners.remove(memberDto);
            if (matchPartners.isEmpty()) {
                matchPartnerCache.evict(fromMemberId);
            }
            log.debug("match list log {}", matchPartners);
        }
    }
}
