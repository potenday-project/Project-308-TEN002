package bside.com.project308.dto.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.Map;
import java.util.Set;

public interface OAuthProviderUser {
    String getId();
    String getUsername();
    String getPassword();
    String getEmail();
    String getProvider();
    Set<? extends GrantedAuthority> getAuthorities();
    Map<String, Object> getAttributes();
}
