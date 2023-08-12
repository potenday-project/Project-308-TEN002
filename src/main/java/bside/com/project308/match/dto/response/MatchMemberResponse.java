package bside.com.project308.match.dto.response;

import bside.com.project308.member.dto.response.MemberResponse;

public record MatchMemberResponse(Long matchId,
                                  Long loginMemberId,
                                  MemberResponse matchedMember) {
}
