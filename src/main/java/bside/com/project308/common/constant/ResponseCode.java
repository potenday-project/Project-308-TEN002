package bside.com.project308.common.constant;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ResponseCode {
    SUCCESS(HttpStatus.OK, 1000, "성공"),
    LOGIN_SUCCESS(HttpStatus.OK, 1101, "로그인 성공"),
    SIGNUP_SUCCESS(HttpStatus.OK, 1102, "회원가입 성공"),
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK, 1103, "회원정보 수정 성공"),
    MEMBER_DELETE_SUCCESS(HttpStatus.OK, 1104, "회원정보 삭제 성공"),
    LOGOUT_SUCCESS(HttpStatus.OK, 1105, "로그아웃 성공"),
    LIKE_POST_SUCCESS(HttpStatus.OK, 1201, "정상적으로 Like요청을 보냈습니다."),
    MATCH_DELETE_SUCCESS(HttpStatus.OK, 1202, "정상적으로 Match를 삭제했습니다."),
    BAD_REQUEST(HttpStatus.OK, 4000, "비정상 접근"),
    NOT_AUTHENTICATED(HttpStatus.OK, 4001, "인증되지 않은 사용자"),
    NOT_AUTHORIZED(HttpStatus.OK, 4002, "자원에 대한 접근 권한 없음"),
    INVALID_TOKEN(HttpStatus.OK, 4003, "유효하지 않은 토큰"),
    MEMBER_NOT_FOUND(HttpStatus.OK, 4100, "해당 Id의 사용자 없음"),
    UNAUTHORIZED_MEMBER_ACCESS(HttpStatus.OK, 4101, "해당 Id는 사용자 정보에 대한 접근 권한 없음"),
    LOGIN_FAIL(HttpStatus.OK, 4102, "기존 가입 정보가 없습니다"),
    SIGN_UP_FAIL(HttpStatus.OK, 4103, "회원 가입에 실패했습니다"),
    BAD_LOGIN_ACCESS(HttpStatus.OK, 4104, "비정상 로그인 시도입니다"),
    MATCH_COUNT_EXHAUSTED(HttpStatus.OK, 4201, "매칭 횟수를 모두 소모했습니다"),
    NO_MORE_PARTNER(HttpStatus.OK, 4202, "매칭 대상자가 더이상 없습니다."),
    MATCH_NOT_FOUND(HttpStatus.OK, 4203, "해당하는 매치 정보가 없습니다"),
    NO_MESSAGE_ROOM(HttpStatus.OK, 4301, "채팅방이 존재하지 않습니다"),
    NOT_AUTHORIZED_ACCESS_TO_MESSAGING(HttpStatus.OK, 4302, "허용되지 않는 채팅기능 접근입니다"),
    UNKNOWN_SERVER_ERROR(HttpStatus.OK, 5000, "알 수 없는 서버 에러");

    private HttpStatus httpStatus;
    private Integer code;
    private String desc;

    ResponseCode(HttpStatus httpStatus, Integer code, String desc) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.desc = desc;
    }
}
