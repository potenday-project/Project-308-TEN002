package bside.com.project308.security.filter;

import bside.com.project308.security.property.SocialLoginProvider;
import bside.com.project308.security.property.SocialLoginRegistration;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
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
public class SocialAuthFilter extends OncePerRequestFilter {

    private final RestTemplate kakaoRestTemplate;
    private final String LOGIN_URL = "/login/oauth2/coddde/kakao";
    private final AntPathRequestMatcher DEFAULT_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL);
    private final SocialLoginProvider socialLoginProvider;
    private final SocialLoginRegistration socialLoginRegistration;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String code = request.getParameter("code");


        URI uri = UriComponentsBuilder.fromHttpUrl(socialLoginProvider.kakao().tokenUri())
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", socialLoginRegistration.kakao().clientId())
                .queryParam("redirect_uri", socialLoginRegistration.kakao().redirectUri())
                .queryParam("code", code)
                .build()
                .encode()
                .toUri();

        RequestEntity loginRequest = RequestEntity.get(uri).build();

        try {
            ResponseEntity<KakaoResponse> kakaoResponse = kakaoRestTemplate.exchange(loginRequest, KakaoResponse.class);
            log.info("resposne {}", kakaoResponse);
            //KakaoResponse kakaoResponse1 = objectMapper.readValue(kakaoResponse.getBody(), KakaoResponse.class);

            ResponseEntity<String> info = getInfo(kakaoResponse.getBody().access_token);
            log.info("{}", info);



        } catch (Exception e) {
            log.error("error", e);
        }



    }


    public ResponseEntity<String> getInfo(String code) {
        URI uri = UriComponentsBuilder.fromHttpUrl(socialLoginProvider.kakao().userInfoUri())
                                      .build()
                                      .encode()
                                      .toUri();

        RequestEntity request = RequestEntity.get(uri)
                                            .header(HttpHeaders.AUTHORIZATION,"Bearer " + code)
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")

                                            .build();

        ResponseEntity<String> exchange = kakaoRestTemplate.exchange(request, String.class);
        return exchange;
    }

    public record KakaoResponse(String token_type,
                                String access_token,
                                String id_token,
                                Integer expires_in,
                                String refresh_token,
                                Integer refresh_token_expired_in,
                                String scope) {
    }

}
