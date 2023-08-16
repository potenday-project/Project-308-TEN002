package bside.com.project308.common.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access =  AccessLevel.PACKAGE)
public class ResponseWithUser extends Response{
    private Long loginMemberId;

    public ResponseWithUser(Integer code, Object message, Object data, Long loginMemberId) {
        super(code, message, data);
        this.loginMemberId = loginMemberId;
    }

    public static <T> ResponseWithUser success(Integer code, T data, Long loginMemberId) {
        return new ResponseWithUser(code, null, data, loginMemberId);
    }

    public static ResponseWithUser success(Integer code, Long loginMemberId) {
        return new ResponseWithUser(code, null, null, loginMemberId);
    }
    public static <U> ResponseWithUser failResponse(Integer code, U message, Long loginMemberId) {
        return new ResponseWithUser(code, message, null, null);
    }
}
