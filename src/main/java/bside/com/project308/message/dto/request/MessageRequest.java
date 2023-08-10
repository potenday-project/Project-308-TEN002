package bside.com.project308.message.dto.request;

public record MessageRequest(
        Long messageRoomId,
        String content
) {
}
