package bside.com.project308.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Response <T>{
    private Integer code;
    private String message;
    private T data;

    public static Response failResponse(Integer code, String message) {
        return new Response(code, message, null);
    }
}
