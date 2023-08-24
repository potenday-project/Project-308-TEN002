package bside.com.project308.match.service;

import bside.com.project308.common.constant.MemberGrade;
import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.match.algorithm.MatchManager;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class MatchService {
    private final MatchRepository matchRepository;
    private final MemberService memberService;
    private final MatchManager matchManager;



    public Match getMatch(Member fromMember, Member toMember) {
        Match match = matchRepository.findMatchByMemberSet(fromMember, toMember).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MATCH_NOT_FOUND));

        return match;
    }

    public List<MemberDto> getTodayMatchPartnerList(Member member) {
        return matchManager.getMatchPartner(member, MemberGrade.INITIAL_MEMBER);
    }

    public Match createMatch(Member fromMember, Member toMember) {
         matchRepository.findMatchByMemberSet(fromMember, toMember).ifPresent(
                 member -> {
                     throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);
                 }
         );

        Match newMatch = Match.of(fromMember, toMember);
        matchRepository.save(newMatch);

        return newMatch;
    }



    public List<MatchDto> getUncheckedMatchList(Long memberId) {
        Member member = memberService.getMemberById(memberId);
        List<Match> matches = matchRepository.findByFromMemberOrToMemberAndCheckedFalse(member, member);
        return matches.stream().map(MatchDto::from).toList();
    }

    public List<MatchDto> getAllMatchList(Long fromMemberId) {
        Member member = memberService.getMemberById(fromMemberId);
        List<Match> matches = matchRepository.findByFromMemberOrToMemberOrderByMatchTimeDesc(member, member);
        return matches.stream().map(MatchDto::from).toList();
    }

    public void deleteMatch(Long fromMemberId, Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MATCH_NOT_FOUND));
        if (match.getFromMember().getId() != fromMemberId && match.getToMember().getId() != fromMemberId) {
            throw new UnAuthorizedAccessException(ResponseCode.NOT_AUTHORIZED);
        }
        //messageRoomService.deleteMessageRoom(match.getFromMember(), match.getToMember());
        matchRepository.delete(match);
    }

    public void checkMatch(Long memberId, Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        //getId는 proxy객체에 대한 select를 수행하지 않는다.
        if (match.getFromMember().getId() != memberId && match.getToMember().getId() != memberId) {
            throw new UnAuthorizedAccessException(ResponseCode.NOT_AUTHORIZED);
        }

        match.checkMatch();
    }

    public MemberDto getMatchedMemberInfo(Long fromMemberId, Long matchId, Long toMemberId) {

        Match match = matchRepository.findById(matchId).orElseThrow(
                () -> new ResourceNotFoundException(ResponseCode.MATCH_NOT_FOUND)
        );


        if (fromMemberId != match.getFromMember().getId() && fromMemberId != match.getToMember().getId()) {
            throw new UnAuthorizedAccessException(ResponseCode.UNAUTHORIZED_MEMBER_ACCESS);
        }


        Long matchedMemberId = fromMemberId == match.getFromMember().getId() ? match.getToMember().getId() : match.getFromMember().getId();
        return memberService.getMemberInfo(matchedMemberId);
    }


}
