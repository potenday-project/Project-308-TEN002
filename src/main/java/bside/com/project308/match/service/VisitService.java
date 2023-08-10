package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Visit;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Optional;
import java.util.Queue;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class VisitService {

    private final VisitRepository visitRepository;
    private final MemberRepository memberRepository;
    private final MatchService matchService;
    private final VisitedMemberCursorRepository visitedMemberCursorRepository;
    private final TodayMatchRepository todayMatchRepository;
    private final CacheManager cacheManager;


    public Optional<MatchDto> postLike(Long fromMemberId, Long toMemberId, boolean like){
        //방문 사용자를 매치 대상자 캐시에서 제외
        evictVisitMember(fromMemberId);



        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        log.debug("delete process start");
        todayMatchRepository.deleteByFromMemberAndToMember(fromMember, toMember);
        log.debug("delete process end");
/*
        Optional<VisitedMemberCursor> byMember = visitedMemberCursorRepository.findByMember(fromMember);
        if (byMember.isPresent()) {
            byMember.get().setLastVisitedMemberId(toMemberId);
        } else{
            visitedMemberCursorRepository.save(new VisitedMemberCursor(fromMember, toMemberId));
        }
*/

        //만약 like기록이 과거에 있던 경우 update
        Visit visit = visitRepository.findByFromMemberAndToMember(fromMember, toMember).orElseGet(() -> Visit.of(fromMember, toMember, like));
        visit.updateLike(like);
        visitRepository.save(visit);

        MatchDto match = null;
        if (like) {
            // 상대방 visit table 검사
            // 상대방도 좋아요 눌렀으면 match table 생성
            //visittable은 삭제
            Optional<Visit> isVisited = visitRepository.findByFromMemberAndToMember(toMember, fromMember);
            if (isVisited.isPresent() && isVisited.get().getIsLike()) {

                match = matchService.match(fromMember, toMember);

                //todo: 양방향을 묶을 수 있는 로직이 필요
                visitRepository.delete(isVisited.get());
                visitRepository.delete(visit);

            }
        }

        return Optional.ofNullable(match);
    }

    private void evictVisitMember(Long fromMemberId) {
        Cache matchPartnerCache = cacheManager.getCache("matchPartner");
        Queue queue = matchPartnerCache.get(fromMemberId, Queue.class);
        if (!CollectionUtils.isEmpty(queue)) {
            queue.poll();
            if (queue.isEmpty()) {
                matchPartnerCache.evict(fromMemberId);
            }
            log.debug("q log {}", queue);
        }
    }
}
