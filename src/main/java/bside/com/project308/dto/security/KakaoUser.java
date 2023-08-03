package bside.com.project308.dto.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class KakaoUser extends AbstractOAuthProviderUser {
    public KakaoUser(OAuth2User oAuth2User, ClientRegistration clientRegistration) {
        super(oAuth2User, clientRegistration, oAuth2User.getAttributes());
    }

    @Override
    public String getId() {
        return String.valueOf(getAttributes().get("id"));
    }

    @Override
    public String getUsername() {
        Map<String, Object> kakaoProps = (Map) getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map)kakaoProps.get("profile");
        return String.valueOf(profile.get("nickname"));
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoProps = (Map) getAttributes().get("kakao_account");
        return String.valueOf(kakaoProps.get("email"));
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoProps = (Map) getAttributes().get("kakao_account");
        Map<String, Object> profile = (Map)kakaoProps.get("profile");
        return String.valueOf(profile.get("nickname"));
    }
}
