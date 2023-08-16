package bside.com.project308.match.dto;

import bside.com.project308.match.entity.Match;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.member.dto.response.SimpleMemberInfo;
import bside.com.project308.member.entity.Member;

import java.time.LocalDateTime;

public record MatchDto(
        Long id,
        MemberDto fromMember,
        MemberDto toMember,
        LocalDateTime matchTime,
        Boolean isChecked) {

    public static MatchDto from(Match match) {
        return new MatchDto(match.getId(),
                MemberDto.from(match.getFromMember()),
                MemberDto.from(match.getToMember()),
                match.getMatchTime(),
                match.isChecked());
    }


}

