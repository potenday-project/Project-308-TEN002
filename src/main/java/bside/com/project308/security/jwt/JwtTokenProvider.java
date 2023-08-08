package bside.com.project308.security.jwt;

import bside.com.project308.security.CustomAuthenticationToken;
import bside.com.project308.security.security.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Getter
    private final String secret;
    @Getter
    private final long tokenTime;
    private Key key;
    public static final String HEADER_PREFIX = "Bearer ";

    public JwtTokenProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.token-time}") long tokenTime) {
        this.secret = secret;
        this.tokenTime = tokenTime * 60 * 1000;
    }

    @PostConstruct
    public void init() {
        log.info("init");
        byte[] keyBytes = Decoders.BASE64.decode(this.secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(UserPrincipal userPrincipal) {

        long current = (new Date()).getTime();

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenTime);

        return Jwts.builder()
                   .setSubject(userPrincipal.getUsername())
                   .claim("id", userPrincipal.id())
                   .claim("authority", userPrincipal.authorities())
                   .claim("userProviderId", userPrincipal.userProviderId())
                   .claim("username", userPrincipal.username())
                   .signWith(key, SignatureAlgorithm.HS512)
                   .setExpiration(validity)
                   .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(token)
                            .getBody();

        UserPrincipal userPrincipal = UserPrincipal.of(
                claims.get("id", Long.class),
                claims.get("userProviderId", String.class),
                claims.get("username", String.class),
                "aaa");

        Authentication authentication = new CustomAuthenticationToken(userPrincipal, userPrincipal.getAuthorities());
        return authentication;
    }

    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
