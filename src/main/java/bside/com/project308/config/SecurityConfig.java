package bside.com.project308.config;

import bside.com.project308.security.CustomAuthenticationEntrypoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final ObjectMapper objectMapper;
    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(authConfigurer -> authConfigurer
                                   .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                                                              .requestMatchers("/", "/home").permitAll()
                                                              .anyRequest().authenticated())
                    .exceptionHandling(exConfigurer -> exConfigurer.authenticationEntryPoint(customAuthenticationEntrypoint()));

        httpSecurity
                    .oauth2Login(oAuthConfigurer -> oAuthConfigurer
                                                        .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(oAuth2UserService)));
        return httpSecurity.build();

    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntrypoint() {
        return new CustomAuthenticationEntrypoint(objectMapper);
    }
}
