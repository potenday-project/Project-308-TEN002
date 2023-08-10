package bside.com.project308.message.dto.response;

import java.util.List;

public record MessageReadResponse(Long loginMemberId,
                                 List<MessageResponse> messages) {
}
