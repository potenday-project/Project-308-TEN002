package bside.com.project308.common.exception.handler;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.*;
import bside.com.project308.common.response.Response;
import ch.qos.logback.core.spi.ErrorCodes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> UnknownServerError(Exception e, HttpServletRequest request) {
        log.error("request url = {}, request Parameter = {}", request.getServletPath(), request.getRequestURL());
        log.error("request ip = ", request.getRemoteHost());

        log.error("Unknown server error", e);
        Response response = Response.failResponse(ResponseCode.UNKNOWN_SERVER_ERROR.getCode(), ResponseCode.UNKNOWN_SERVER_ERROR.getDesc());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler({InvalidAccessException.class, UnAuthorizedAccessException.class, ResourceNotFoundException.class, DuplicatedMemberException.class})
    public ResponseEntity<Response> InvalidExceptionHandler(BaseException e) {
        log.error("BaseException", e);

        Response response = Response.failResponse(e.getResponseCode().getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response> httpMethodExHandler(HttpRequestMethodNotSupportedException e) {
        log.error("Not Supported Method", e);

        Response response = Response.failResponse(ResponseCode.BAD_REQUEST.getCode(), "지원하지 않는 메소드입니다.");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> requestValidationEx(MethodArgumentNotValidException e) {
        log.error("Argument Validation Exception", e);
        Map<String, List<String>> messageDetail = convertBindingResultToMap(e.getBindingResult());

        Response response = Response.failResponse(ResponseCode.BAD_REQUEST.getCode(), messageDetail);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    private static Map<String, List<String>> convertBindingResultToMap(BindingResult bindingResult) {
        log.info("error : {}", bindingResult.getFieldErrors());
        Map<String, List<String>> messageDetail = new HashMap<>();



        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors()
                         .forEach(e -> messageDetail
                                 .computeIfAbsent(e.getField(), key -> new ArrayList<String>())
                                 .add(e.getDefaultMessage()));
        }

        return messageDetail;
    }

}
