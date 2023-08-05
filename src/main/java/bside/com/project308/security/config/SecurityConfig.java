package bside.com.project308.security.config;

import bside.com.project308.security.filter.CustomLoginFilter;
import bside.com.project308.security.security.CustomAccessDeniedHandler;
import bside.com.project308.security.security.CustomAuthenticationEntrypoint;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final OAuth2UserService oAuth2UserService;
    private final ObjectMapper objectMapper;
    private final CustomLoginFilter customLoginFilter;
    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {

        httpSecurity.csrf(csrfConfigurer -> csrfConfigurer.disable());

        httpSecurity.authorizeHttpRequests(authConfigurer -> authConfigurer
                                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/member/sign-up"), new AntPathRequestMatcher("/member/skill", "GET")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/**")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/**")).permitAll()
                                    .anyRequest().authenticated()
                            )
                .addFilterAfter(customLoginFilter, LogoutFilter.class)
                .headers(headerConfigurer -> headerConfigurer.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                    .exceptionHandling(exConfigurer -> exConfigurer
                                   .authenticationEntryPoint(customAuthenticationEntrypoint())
                                    .accessDeniedHandler(customAccessDeniedHandler()));

/*        httpSecurity
                    .oauth2Login(oAuthConfigurer -> oAuthConfigurer
                                                        .userInfoEndpoint(userInfo -> userInfo
                                                                .userService(oAuth2UserService)
                                                                )

                            .defaultSuccessUrl("/info")
                            );*/
        return httpSecurity.build();

    }



    @Bean
    public AuthenticationEntryPoint customAuthenticationEntrypoint() {
        return new CustomAuthenticationEntrypoint(objectMapper);
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }
}
