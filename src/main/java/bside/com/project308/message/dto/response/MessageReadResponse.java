package bside.com.project308.message.dto.response;

import java.util.List;

public record MessageReadResponse(Long loginMemberId,
                                 String partnerName,
                                 Long partnerId,
                                 Long matchId,
                                 List<MessageResponse> messages) {
}
