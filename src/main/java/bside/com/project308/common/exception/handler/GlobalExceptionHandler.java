package bside.com.project308.common.exception.handler;

import bside.com.project308.common.exception.BaseException;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.common.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void UnknwonExceptionHandler(Exception e) {
        log.error("error", e);
    }

    @ExceptionHandler({InvalidAccessException.class, UnAuthorizedAccessException.class, ResourceNotFoundException.class})
    public ResponseEntity<Response> InvalidExceptionHandler(BaseException e) {
        log.error("invalid ex", e);

        Response response = Response.failResponse(e.getResponseCode().getCode(), e.getMessage());
        return ResponseEntity.status(e.getHttpStatusCode()).body(response);
    }

}
