package bside.com.project308.match.dto.response;

public record SwipeResponse(Integer usedCount,
                            Boolean isMatched,
                            MatchResponse matchInfo,
                            Long messageRoomId) {
}
