package bside.com.project308.security.util.oauthconverter;

import bside.com.project308.common.constant.RegistrationId;
import bside.com.project308.security.security.UserPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class KakaoUserConverter implements OauthConverter{
    @Override
    public boolean support(String registrationId) {
        if (RegistrationId.KAKAO.getRegistrationId().equals(registrationId)) {
            return true;
        }

        return false;
    }

    @Override
    public UserPrincipal convert(OAuth2User oAuth2User) {
        Map<String, Object> kakaoAttributes = oAuth2User.getAttributes();
        String id = String.valueOf(kakaoAttributes.get("id"));

        Map<String, Object> kakaoProps = (Map) kakaoAttributes.get("kakao_account");
        String email = String.valueOf(kakaoProps.get("email"));

        Map<String, Object> profile = (Map) kakaoProps.get("profile");
        String username = String.valueOf(profile.get("nickname"));

        return UserPrincipal.of(null, id, username, UUID.randomUUID().toString(), null, kakaoAttributes);
    }
}
