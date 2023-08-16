package bside.com.project308.security.security;

import bside.com.project308.common.config.CacheConfig;
import bside.com.project308.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutHandler implements LogoutHandler {
    private final CacheManager cacheManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        registerInExpiredToken(authentication);
    }

    private void registerInExpiredToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        if (userPrincipal.token() == null) {
            return;
        }
        String token = userPrincipal.token();
        cacheManager.getCache(CacheConfig.CACHE_NAME_MATH_EXPIRED_TOKEN).put(token, JwtTokenProvider.TOKEN_EXPIRED);
        log.debug("{} is expired", token);
    }
}
