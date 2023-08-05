package bside.com.project308.match.algorithm;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.entity.Visit;
import bside.com.project308.match.entity.VisitedMemberCursor;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BasicMatchAlgorithm implements MatchAlgorithm{
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final VisitRepository visitRepository;
    private final VisitedMemberCursorRepository visitedMemberCursorRepository;
    private final MatchRepository matchRepository;
    private final MemberService memberService;
    @Override
    public MemberDto getMatchPartner(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<Interest> interests = interestRepository.findByMember(member);
        Long latVisitedMemberId = visitedMemberCursorRepository.findByMember(member).map(VisitedMemberCursor::getId).orElse(1L);
        List<Position> positions = interests.stream().map(interest -> Position.valueOf(interest.getInterest())).toList();


        Set<Member> partnerCandidates = memberRepository.findTop50ByIdGreaterThanAndPositionInAndIdNot(latVisitedMemberId,
                                                                                                        positions,
                                                                                                        member.getId());
        Set<Member> visitedMembers = visitRepository.findByFromMember(member).stream().map(Visit::getToMember).collect(Collectors.toSet());
        Set<Member> collect = matchRepository.findByFromMember(member)
                                             .stream()
                                             .parallel()
                                             .map(Match::getToMember)
                                             .collect(Collectors.toSet());

        partnerCandidates.removeAll(visitedMembers);
        partnerCandidates.removeAll(collect);
        List<Member> resultCandidate = partnerCandidates.stream().collect(Collectors.toList());
        resultCandidate.sort(Comparator.comparing(Member::getId));

        Member targetMember = resultCandidate.get(0);
        //lastVisitedCursor.setLastVisitedMemberId(targetMember.getId());
        return memberService.getMemberInfo(targetMember.getId());
    }
}
