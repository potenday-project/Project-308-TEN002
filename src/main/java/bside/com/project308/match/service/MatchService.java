package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.algorithm.MatchAlgorithm;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

    }

    public List<MatchDto> getUncheckedMatchList(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        List<Match> matches = matchRepository.findByFromMemberAndCheckedFalse(member);
        return matches.stream().map(MatchDto::from).toList();
    }
}
