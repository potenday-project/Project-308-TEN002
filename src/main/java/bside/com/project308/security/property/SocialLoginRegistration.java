package bside.com.project308.security.property;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.security.oauth2.client.registration")
public record SocialLoginRegistration(KakaoProps kakao) {


    public record KakaoProps(String clientId,
                             String authorizationGrantType,
                             String redirectUri,
                             String[] scope){

    }


}
