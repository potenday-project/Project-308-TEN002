package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.algorithm.MatchAlgorithm;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Transactional
public class MatchService {

    private final MatchAlgorithm matchAlgorithm;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    public MemberDto getMatchPartner(Long memberId) {
        return matchAlgorithm.getMatchPartner(memberId);
    }


    public void match(Member fromMember, Member toMember) {
         matchRepository.findByFromMemberAndToMember(fromMember, toMember).ifPresent(
                 member -> {
                     throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);
                 }
         );

        Match newMatch1 = Match.of(fromMember, toMember);
        Match newMatch2 = Match.of(toMember, fromMember);

        matchRepository.save(newMatch1);
        matchRepository.save(newMatch2);
        newMatch1.connectMatch(newMatch2);

        //todo: 양방향을 묶을 수 있는 로직이 필요

    }

    public List<MatchDto> getUncheckedMatchList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<Match> matches = matchRepository.findByFromMemberAndCheckedFalse(member);
        return matches.stream().map(MatchDto::from).toList();
    }

    public List<MatchDto> getAllMatchList(Long fromMemberId) {
        Member member = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<Match> matches = matchRepository.findByFromMemberOrderByMatchTimeDesc(member);
        return matches.stream().map(MatchDto::from).toList();
    }

    public void deleteMatch(Long fromMemberId, Long matchId) {
        matchRepository.deleteByFromMemberIdAndId(fromMemberId, matchId);

        //todo: 양방향을 묶을 수 있는 로직이 필요

    }
}
