package bside.com.project308.security.security;

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
        cacheManager.getCache("expiredToken").put(token, "expired");
        log.debug("{} is expired", token);
    }
}
