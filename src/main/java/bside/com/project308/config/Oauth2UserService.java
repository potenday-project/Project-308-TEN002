package bside.com.project308.config;

import bside.com.project308.constant.RegistrationId;
import bside.com.project308.dto.UserPrincipal;
import bside.com.project308.dto.security.KakaoUser;
import bside.com.project308.dto.security.OAuthProviderUser;
import bside.com.project308.exception.Oauth2ConvertException;
import bside.com.project308.util.oauthconverter.OauthConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Oauth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final List<OauthConverter> converters;

    public Oauth2UserService(List<OauthConverter> converters) {
        this.converters = converters;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        OAuth2UserService delegateService = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegateService.loadUser(userRequest);
        for (OauthConverter converter : converters) {
            if(converter.support(clientRegistration.getRegistrationId())){
                return converter.convert(oAuth2User);
            }

        }

        throw new Oauth2ConvertException();
    }

}
