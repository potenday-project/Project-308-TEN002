package bside.com.project308.match.dto.response;

import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.entity.Match;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.SimpleMemberInfo;

import java.time.LocalDateTime;

public record MatchResponse(
        Long id,
        SimpleMemberInfo fromMember,
        SimpleMemberInfo toMember,
        LocalDateTime matchTime,
        boolean isChecked) {
    public static MatchResponse from(MatchDto matchDto) {
        MemberDto fromMember = matchDto.fromMember();
        MemberDto toMember = matchDto.toMember();
        return new MatchResponse(matchDto.id(),
                new SimpleMemberInfo(fromMember.id(), fromMember.username(), fromMember.position(), fromMember.imgUrl()),
                new SimpleMemberInfo(toMember.id(), toMember.username(), toMember.position(), toMember.imgUrl()),
                matchDto.matchTime(),
                matchDto.isChecked());
    }
}
