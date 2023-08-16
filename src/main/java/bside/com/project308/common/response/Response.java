package bside.com.project308.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Response <T, U>{
    private Integer code;
    private U message;
    private T data;

    public static <T> Response success(Integer code, T data) {
        return new Response(code, null, data);
    }

    public static Response success(Integer code) {
        return new Response(code, null, null);
    }
    public static <U> Response failResponse(Integer code, U message) {
        return new Response(code, message, null);
    }

}
