package bside.com.project308.security.filter;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.notification.constant.NotificationType;
import bside.com.project308.notification.dto.NotificationResponse;
import bside.com.project308.security.jwt.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomJwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CacheManager cacheManager;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final ObjectMapper objectMapper;
    private final AntPathRequestMatcher requestMatcher = new AntPathRequestMatcher("/subscribe/**");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("request : {}, token: {}", request.getServletPath(), token);
/*
        if (requestMatcher.matches(request)) {
            invalidTokenHandler(request, response);
            return;
        }
*/

        if (!StringUtils.hasLength(token) || !token.startsWith(JwtTokenProvider.HEADER_PREFIX)) {
            filterChain.doFilter(request, response);

            return;
        }

        String processedToken = tokenValidationCheckAndGet(token);

        if (processedToken != null) {
            boolean isTokeValid = jwtTokenProvider.validateToken(processedToken);
            if(isTokeValid){
                Authentication authentication = jwtTokenProvider.getAuthentication(processedToken);

                SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
                context.setAuthentication(authentication);
                SecurityContextHolder.getContextHolderStrategy().setContext(context);

                log.debug("information : {}", authentication);
                log.debug("authenticated");
                filterChain.doFilter(request, response);

            }else{
                invalidTokenHandler(request, response);
            }
        }
    }
    private void invalidTokenHandler(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.error("[Application Error] Invalid token!");
        this.securityContextHolderStrategy.clearContext();
        if (requestMatcher.matches(request)) {
            sseSubscribeRequest(response);
        } else{
            generalRequest(response);
        }
    }

    private void sseSubscribeRequest(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
        response.setStatus(HttpStatus.OK.value());
        response.setCharacterEncoding("UTF-8");
//        response.getWriter().println("retry: 10000\n\n");
//        response.getWriter().println("data: My message \n\n");

        //Response errorResponse = Response.failResponse(ResponseCode.SUCCESS.getCode(), "토큰에 이상이 있습니다.");
        NotificationResponse responseWithId = new NotificationResponse(UUID.randomUUID().toString().substring(0, 5), NotificationType.ERROR, "token error");
        Response errorResponse = Response.success(ResponseCode.SUCCESS.getCode(), responseWithId);
        String result = objectMapper.writeValueAsString(errorResponse);
        log.debug("{}", result);
        response.getWriter().println("\n\n" + "data: " + result + "\n\n");


     /*   Response.failResponse()

        String body = "data: My message \n\n";
        log.info("{}", body);
        //response.getWriter().println(body);
        ServletServerHttpResponse servletServerHttpResponse = new ServletServerHttpResponse(response);

        body = "retry: 5000\n\n";
        StreamUtils.copy(body, StandardCharsets.UTF_8, servletServerHttpResponse.getBody());
        log.info("new log");
        for (int i = 0; i < 10; i++) {
            try {
                sleep(3000);
            } catch (Exception e) {

            }

            body = "data: My message" + i+ "\n\n";

            log.info("{}", "My message");
            StreamUtils.copy(body, StandardCharsets.UTF_8, servletServerHttpResponse.getBody());
        }*/

        //SseEmitter emitter = new SseEmitter(100L);

       /* try {
            emitter.send(SseEmitter
                    .event()
                    .id("1")
                    .data("data")

            );
        } catch (Exception e) {
            log.error("", e);
        }*/

        /*new Thread(() -> {

            try {
                sleep(2 * 1000);
                SseEmitter emitter2 = new SseEmitter();
                emitter2.send(SseEmitter.event().data("wow").id("1"));
                emitter2.complete();
                log.info("data send");
            } catch (Exception e) {
                log.info("error");
                throw new RuntimeException(e);
            }

        }).start();*/

    }

    private void generalRequest(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Response errorResponse = Response.failResponse(ResponseCode.INVALID_TOKEN.getCode(), ResponseCode.INVALID_TOKEN.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }

    private String tokenValidationCheckAndGet(String token) {
        token = token.replace(JwtTokenProvider.HEADER_PREFIX, "");

        String expiredToken = cacheManager.getCache("expiredToken").get(token, String.class);
        if (StringUtils.hasText(expiredToken) && expiredToken.equals(JwtTokenProvider.TOKEN_EXPIRED)) {
            token = null;
        }
        return token;
    }
}
