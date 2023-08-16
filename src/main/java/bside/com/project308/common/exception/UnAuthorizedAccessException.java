package bside.com.project308.common.exception;

import bside.com.project308.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class UnAuthorizedAccessException extends BaseException{

    public UnAuthorizedAccessException(ResponseCode responseCode) {
        super(HttpStatus.FORBIDDEN, responseCode);
    }

    public UnAuthorizedAccessException(ResponseCode responseCode, String message) {
        super(HttpStatus.FORBIDDEN, responseCode, message);
    }
}
