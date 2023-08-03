package bside.com.project308.dto.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

public abstract class AbstractOAuthProviderUser implements OAuthProviderUser, OAuth2User{

    private final OAuth2User oAuth2User;
    private final ClientRegistration clientRegistration;
    private Map<String, Object> attributes;

    public AbstractOAuthProviderUser(OAuth2User oAuth2User, ClientRegistration clientRegistration, Map<String, Object> attributes) {
        this.oAuth2User = oAuth2User;
        this.clientRegistration = clientRegistration;
        this.attributes = attributes;
    }

    @Override
    public String getPassword() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getProvider() {
        return clientRegistration.getRegistrationId();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Set<? extends GrantedAuthority> getAuthorities() {
        return new LinkedHashSet<>(oAuth2User.getAuthorities());
    }
}
