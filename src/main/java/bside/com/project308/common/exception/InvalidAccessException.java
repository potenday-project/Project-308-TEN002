package bside.com.project308.common.exception;

import bside.com.project308.common.constant.ResponseCode;
import org.springframework.http.HttpStatusCode;

public class InvalidAccessException extends BaseException{
    public InvalidAccessException(HttpStatusCode httpStatusCode, ResponseCode responseCode) {
        super(httpStatusCode, responseCode);
    }

    public InvalidAccessException(HttpStatusCode httpStatusCode, ResponseCode responseCode, String message) {
        super(httpStatusCode, responseCode, message);
    }

    public InvalidAccessException(HttpStatusCode httpStatusCode, ResponseCode responseCode, String message, Throwable throwable) {
        super(httpStatusCode, responseCode, message, throwable);
    }
}
