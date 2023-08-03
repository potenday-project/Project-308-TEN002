package bside.com.project308.util.oauthconverter;

import bside.com.project308.dto.UserPrincipal;
import bside.com.project308.dto.security.OAuthProviderUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OauthConverter {
    public boolean support(String registrationId);
    public UserPrincipal convert(OAuth2User oAuth2User);

}
