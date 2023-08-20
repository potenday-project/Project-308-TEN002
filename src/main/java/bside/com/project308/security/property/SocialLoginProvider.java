package bside.com.project308.security.property;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties("spring.security.oauth2.client.provider")
public record SocialLoginProvider(KakaoProps kakao) {


    public record KakaoProps(String authorizationUri,
                             String tokenUri,
                             String userInfoUri,
                             String userNameAttribute){

    }
}
