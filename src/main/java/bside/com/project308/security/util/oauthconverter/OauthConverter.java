package bside.com.project308.security.util.oauthconverter;

import bside.com.project308.security.security.UserPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OauthConverter {
    public boolean support(String registrationId);
    public UserPrincipal convert(OAuth2User oAuth2User);

}
