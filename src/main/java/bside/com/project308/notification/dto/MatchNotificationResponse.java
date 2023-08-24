package bside.com.project308.notification.dto;

import bside.com.project308.match.dto.response.MatchResponse;

public record MatchNotificationResponse(MatchResponse matchInfo,
                                        Long messageRoomId) {
}
