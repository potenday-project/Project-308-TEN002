package bside.com.project308.security.security;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
      log.info("access dined");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Response errorResponse = Response.failResponse(ResponseCode.NOT_AUTHORIZED.getCode(), ResponseCode.NOT_AUTHORIZED.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }
}
