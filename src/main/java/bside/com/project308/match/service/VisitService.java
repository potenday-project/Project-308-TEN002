package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.entity.Visit;
import bside.com.project308.match.entity.VisitedMemberCursor;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class VisitService {

    private final VisitRepository visitRepository;
    private final MemberRepository memberRepository;
    private final MatchService matchService;
    private final VisitedMemberCursorRepository visitedMemberCursorRepository;


    @CacheEvict(value = "matchPartner", key = "#fromMemberId")
    public Boolean postLike(Long fromMemberId, Long toMemberId, boolean like){
        boolean flag = false;


        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        Optional<VisitedMemberCursor> byMember = visitedMemberCursorRepository.findByMember(fromMember);
        if (byMember.isPresent()) {
            byMember.get().setLastVisitedMemberId(toMemberId);
        } else{
            visitedMemberCursorRepository.save(new VisitedMemberCursor(fromMember, toMemberId));
        }

        Visit visit = Visit.of(fromMember, toMember, like);
        visitRepository.save(visit);

        if (like) {
            // 상대방 visit table 검사
            // 상대방도 좋아요 눌렀으면 match table 생성
            //visittable은 삭제
            Optional<Visit> isVisited = visitRepository.findByFromMemberAndToMember(toMember, fromMember);
            if (isVisited.isPresent() && isVisited.get().getMatchResult()) {
                matchService.match(fromMember, toMember);

                //todo: 양방향을 묶을 수 있는 로직이 필요
                visitRepository.delete(isVisited.get());
                visitRepository.delete(visit);
                flag = true;
            }
        }

        return flag;
    }
}
