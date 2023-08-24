package bside.com.project308.security.filter;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.security.jwt.JwtTokenProvider;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomJwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CacheManager cacheManager;
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final ObjectMapper objectMapper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("request : {}, token: {}", request.getServletPath(), token);

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
    protected void invalidTokenHandler(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.error("[Application Error] Invalid token!");
        this.securityContextHolderStrategy.clearContext();
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
