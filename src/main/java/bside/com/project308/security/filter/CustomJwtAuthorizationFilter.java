package bside.com.project308.security.filter;

import bside.com.project308.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
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


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (!StringUtils.hasLength(token) || !token.startsWith(JwtTokenProvider.HEADER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String processedToken = tokenValidationCheckAndGet(token);

        if (processedToken != null && jwtTokenProvider.validateToken(processedToken)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(processedToken);

            SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.getContextHolderStrategy().setContext(context);

            log.info("information : {}", authentication);
            log.info("authenticated");
        }

        filterChain.doFilter(request, response);

    }

    private String tokenValidationCheckAndGet(String token) {
        token = token.replace(JwtTokenProvider.HEADER_PREFIX, "");

        String expiredToken = cacheManager.getCache("expiredToken").get(token, String.class);
        if (StringUtils.hasText(expiredToken) && expiredToken.equals("expired")) {
            token = null;
        }
        return token;
    }
}
