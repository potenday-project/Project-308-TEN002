package bside.com.project308.security.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record UserPrincipal(
        Long id,
        String userProviderId,
        String username,
        String password,
        String token,
        Collection<? extends GrantedAuthority> authorities,
        Map<String, Object> oAuth2Attributes

) implements UserDetails, OAuth2User {

    public static UserPrincipal of(Long id, String userProviderId, String username, String password, String token){
        return new UserPrincipal(
                id,
                userProviderId,
                username,
                password,
                token,
                AuthorityUtils.createAuthorityList("USER"),
                null);
    }
    public static UserPrincipal of(Long id, String userProviderId, String username, String password, String token, Map<String, Object> oAuth2Attributes){
        return new UserPrincipal(
                id,
                userProviderId,
                username,
                password,
                token,
                AuthorityUtils.createAuthorityList("USER"),
                oAuth2Attributes);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {return false;}
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {return false;}
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {return false;}
    @JsonIgnore
    @Override
    public boolean isEnabled() {return false;}

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }

    @JsonIgnore
    @Override
    public String getName() {
        return username;
    }
}
