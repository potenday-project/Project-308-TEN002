package bside.com.project308.match.dto.request;

import lombok.Getter;

public record MatchRequest(Long toMemberId,
                           Boolean like) {
}
