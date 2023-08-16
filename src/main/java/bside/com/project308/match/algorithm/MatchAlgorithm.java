package bside.com.project308.match.algorithm;

import bside.com.project308.member.dto.MemberDto;

import java.util.Collection;
import java.util.List;

public interface MatchAlgorithm {
    public MemberDto getMatchPartner(Long memberId);
    public List<MemberDto> getTodayMatchPartner(Long memberId);
}
