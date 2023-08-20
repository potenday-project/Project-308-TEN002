package bside.com.project308.security.filter;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.security.jwt.JwtTokenProvider;
import bside.com.project308.security.property.SocialLoginProvider;
import bside.com.project308.security.property.SocialLoginRegistration;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Component
@Slf4j
public class SocialLoginFilter extends OncePerRequestFilter {

    private final RestTemplate kakaoRestTemplate;
    private final String LOGIN_URL = "/login-social";
    private final AntPathRequestMatcher DEFAULT_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL, "GET");
    private final SocialLoginProvider socialLoginProvider;
    private final SocialLoginRegistration socialLoginRegistration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        URI uri = UriComponentsBuilder.fromHttpUrl(socialLoginProvider.kakao().authorizationUri())
                .queryParam("client_id", socialLoginRegistration.kakao().clientId())
                .queryParam("redirect_uri", socialLoginRegistration.kakao().redirectUri())
                .queryParam("response_type", "code")
                .build()
                .encode()
                .toUri();

        response.sendRedirect(uri.toString());
        /*RequestEntity loginRequest = RequestEntity.get(uri).build();
        try {
            ResponseEntity<String> kakaoResponse = kakaoRestTemplate.exchange(loginRequest, String.class);


            response.setContentType("text/html");

            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().println(kakaoResponse.getBody());


            log.info("resposne {}", kakaoResponse);
        } catch (Exception e) {
            log.error("error", e);
        }*/



    }
}
