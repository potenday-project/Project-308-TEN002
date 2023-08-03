package bside.com.project308.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    SUCCESS(HttpStatus.OK, 1000, "성공"),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, 4001, "인증되지 않은 사용자"),
    NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, 4002, "자원에 대한 접근 권한 없음");

    private HttpStatus httpStatus;
    private Integer code;
    private String desc;

    ResponseCode(HttpStatus httpStatus, Integer code, String desc) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.desc = desc;
    }
}
