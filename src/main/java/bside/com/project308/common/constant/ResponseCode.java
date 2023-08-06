package bside.com.project308.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    SUCCESS(HttpStatus.OK, 1000, "성공"),
    LOGIN_SUCCESS(HttpStatus.OK, 1101, "로그인 성공"),
    SIGNUP_SUCCESS(HttpStatus.OK, 1102, "회원가입 성공"),
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK, 1103, "회원정보 수정 성공"),
    LIKE_POST_SUCCESS(HttpStatus.OK, 1201, "정상적으로 Like요청을 보냈습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 4000, "비정상 접근"),
    NOT_AUTHENTICATED(HttpStatus.UNAUTHORIZED, 4001, "인증되지 않은 사용자"),
    NOT_AUTHORIZED(HttpStatus.FORBIDDEN, 4002, "자원에 대한 접근 권한 없음"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, 4100, "해당 Id의 사용자 없음"),
    UNAUTHORIZED_MEMBER_ACCESS(HttpStatus.FORBIDDEN, 4101, "해당 Id는 사용자 정보에 대한 접근 권한 없음"),
    LOGIN_FAIL(HttpStatus.NOT_FOUND, 4103, "기존 가입 정보가 없습니다"),
    SIGN_UP_FAIL(HttpStatus.BAD_REQUEST, 4104, "회원 가입에 실패했습니다"),
    MATCH_COUNT_EXHAUSTED(HttpStatus.BAD_REQUEST, 4201, "매칭 횟수를 모두 소모했습니다");

    private HttpStatus httpStatus;
    private Integer code;
    private String desc;

    ResponseCode(HttpStatus httpStatus, Integer code, String desc) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.desc = desc;
    }
}
