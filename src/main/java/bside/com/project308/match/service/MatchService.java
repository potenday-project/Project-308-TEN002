package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.match.algorithm.MatchAlgorithm;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.service.MessageRoomService;
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

    private final MatchAlgorithm matchAlgorithm;
    private final MatchRepository matchRepository;
    private final MemberRepository memberRepository;
    //todo : 향후 event방식으로 변경해서 결합도를 낮춰야함
    private final MessageRoomService messageRoomService;
    private final MemberService memberService;


    public MatchDto getMatch(Long fromMemberId, Long toMemberId) {
        Member fromMember = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Member toMember = memberRepository.findById(toMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));

        Match match = matchRepository.findByFromMemberAndToMember(fromMember, toMember).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MATCH_NOT_FOUND));

        return MatchDto.from(match);
    }

    public List<MemberDto> getTodayMatchPartner(Long memberId) {
        return matchAlgorithm.getTodayMatchPartner(memberId);
    }
    public MemberDto getMatchPartner(Long memberId) {
        return matchAlgorithm.getMatchPartner(memberId);
    }

    public MatchDto createMatch(Member fromMember, Member toMember) {
         matchRepository.findByFromMemberAndToMember(fromMember, toMember).ifPresent(
                 member -> {
                     throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.BAD_REQUEST);
                 }
         );

        Match newMatch = Match.of(fromMember, toMember);
        matchRepository.save(newMatch);


        messageRoomService.createMessageRoom(fromMember, toMember, newMatch);
        //todo: 양방향을 묶을 수 있는 로직이 필요
        return MatchDto.from(newMatch);
    }

    public List<MatchDto> getUncheckedMatchList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<Match> matches = matchRepository.findByFromMemberOrToMemberAndCheckedFalse(member, member);
        return matches.stream().map(MatchDto::from).toList();
    }

    public List<MatchDto> getAllMatchList(Long fromMemberId) {
        Member member = memberRepository.findById(fromMemberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
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
