package bside.com.project308.match.dto.response;

import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.member.dto.MemberDto;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        MemberDto fromMember,
        MemberDto toMember,
        LocalDateTime matchTime) {
    public static MatchResponse from(MatchDto matchDto) {
        return new MatchResponse(matchDto.id(), matchDto.fromMember(), matchDto.toMember(), matchDto.matchTime());
    }
}
