package bside.com.project308.security.filter;

import bside.com.project308.security.property.KakaoOAuth2Response;
import bside.com.project308.security.property.SocialLoginProvider;
import bside.com.project308.security.property.SocialLoginRegistration;
import com.nimbusds.common.contenttype.ContentType;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RequiredArgsConstructor
@Slf4j
@Component
public class OauthLoginFilter extends OncePerRequestFilter {
    private final String LOGIN_URL = "/login/oauth2/code/{registrationId}";
    private final String REGISTRATION_ID_URI_VARIABLE_NAME = "registrationId";
    private final AntPathRequestMatcher DEFAULT_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL);
    private final SocialLoginProvider socialLoginProvider;
    private final SocialLoginRegistration socialLoginRegistration;
    private final RestTemplate kakaoRestTemplate;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationCode = request.getParameter("code");
        URI uri = UriComponentsBuilder.fromHttpUrl(socialLoginProvider.kakao().tokenUri())
                                      .queryParam("grant_type", "authorization_code")
                                      .queryParam("client_id", socialLoginRegistration.kakao().clientId())
                                      .queryParam("redirect_uri", socialLoginRegistration.kakao().redirectUri())
                                      .queryParam("code", authorizationCode)
                                      .build()
                                      .encode()
                                      .toUri();

        RequestEntity authorizationRequest = RequestEntity.get(uri).build();
        try {
            ResponseEntity<KakaoAuthorizationResponse> kakaoResponse = kakaoRestTemplate.exchange(authorizationRequest, KakaoAuthorizationResponse.class);
            ResponseEntity<KakaoOAuth2Response> info = getInfo(kakaoResponse.getBody().access_token);
            log.info("information : {}", info);
            response.setContentType(ContentType.APPLICATION_JSON.getType());
            response.getWriter().println(info.getBody());
        } catch (HttpClientErrorException e) {
            log.error("카카오 에러", e);
        } catch (Exception e) {
            log.error("기타 에러", e);
        }


/*

        try {
            ResponseEntity<SocialAuthFilter.KakaoResponse> kakaoResponse = kakaoRestTemplate.exchange(loginRequest, SocialAuthFilter.KakaoResponse.class);
            log.info("resposne {}", kakaoResponse);
            //KakaoResponse kakaoResponse1 = objectMapper.readValue(kakaoResponse.getBody(), KakaoResponse.class);

            ResponseEntity<String> info = getInfo(kakaoResponse.getBody().access_token);
            log.info("{}", info);


        } catch(HttpClientErrorException e){

        }
        catch (Exception e) {
            log.error("error", e);
        }

*/

    }

    public ResponseEntity<KakaoOAuth2Response> getInfo(String code) {
        URI uri = UriComponentsBuilder.fromHttpUrl(socialLoginProvider.kakao().userInfoUri())
                                      .build()
                                      .encode()
                                      .toUri();

        RequestEntity request = RequestEntity.get(uri)
                                             .header(HttpHeaders.AUTHORIZATION,"Bearer " + code)
                                             .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8")

                                             .build();

        ResponseEntity<KakaoOAuth2Response> exchange = kakaoRestTemplate.exchange(request, KakaoOAuth2Response.class);
        return exchange;
    }

    private String resolveAuthorizationRequestUrl(HttpServletRequest request) {
        if (this.DEFAULT_REQUEST_MATCHER.matches(request)) {
            return this.DEFAULT_REQUEST_MATCHER.matcher(request).getVariables()
                                               .get(REGISTRATION_ID_URI_VARIABLE_NAME);
        };
        return null;
    }

    public record KakaoAuthorizationResponse(String token_type,
                                String access_token,
                                String id_token,
                                Integer expires_in,
                                String refresh_token,
                                Integer refresh_token_expired_in,
                                String scope) {
    }


}
