package bside.com.project308.common.response;

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

    public static <T> Response success(Integer code, T data) {
        return new Response(code, null, data);
    }

    public static <T> Response success(Integer code) {
        return new Response(code, null, null);
    }
    public static Response failResponse(Integer code, String message) {
        return new Response(code, message, null);
    }
}
