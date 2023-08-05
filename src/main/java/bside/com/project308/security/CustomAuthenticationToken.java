package bside.com.project308.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private UserDetails userDetails;

    public CustomAuthenticationToken(UserDetails userDetails, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userDetails = userDetails;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userDetails;
    }
}
