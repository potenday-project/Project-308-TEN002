package bside.com.project308.match.algorithm;

import bside.com.project308.member.dto.MemberDto;

public interface MatchAlgorithm {
    public MemberDto getMatchPartner(Long memberId);
}
