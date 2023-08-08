package bside.com.project308.match.dto.response;

import bside.com.project308.match.dto.MatchDto;

public record LikeResponse(Integer usedCount,
                           Boolean isMatched,
                           MatchResponse matchInfo,
                           Long messageRoomId) {
}
