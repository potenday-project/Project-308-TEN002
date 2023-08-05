package bside.com.project308.common.exception;

import bside.com.project308.common.constant.ResponseCode;
import ch.qos.logback.core.spi.ErrorCodes;
import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BaseException extends RuntimeException{
    private ResponseCode responseCode;
    private HttpStatusCode httpStatusCode;

    public BaseException(HttpStatusCode httpStatusCode, ResponseCode responseCode) {
        this.httpStatusCode = httpStatusCode;
        this.responseCode = responseCode;
    }

    public BaseException(HttpStatusCode httpStatusCode, ResponseCode responseCode, String message) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.responseCode = responseCode;
    }
    public BaseException(HttpStatusCode httpStatusCode, ResponseCode responseCode, String message, Throwable throwable) {
        super(message, throwable);
        this.httpStatusCode = httpStatusCode;
        this.responseCode = responseCode;
    }
}
