package bside.com.project308.common.exception;

import bside.com.project308.common.constant.ResponseCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class ResourceNotFoundException extends BaseException{

    public ResourceNotFoundException(ResponseCode responseCode) {
        super(HttpStatus.NOT_FOUND, responseCode);
    }

    public ResourceNotFoundException(ResponseCode responseCode, String message) {
        super(HttpStatus.NOT_FOUND, responseCode, message);
    }

    public ResourceNotFoundException(ResponseCode responseCode, String message, Throwable throwable) {
        super(HttpStatus.NOT_FOUND, responseCode, message, throwable);
    }
}
