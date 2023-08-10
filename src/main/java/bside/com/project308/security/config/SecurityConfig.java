package bside.com.project308.security.config;

import bside.com.project308.security.filter.CustomJwtAuthorizationFilter;
import bside.com.project308.security.filter.CustomJwtLoginFilter;
import bside.com.project308.security.filter.CustomLoginFilter;
import bside.com.project308.security.security.CustomAccessDeniedHandler;
import bside.com.project308.security.security.CustomAuthenticationEntrypoint;
import bside.com.project308.security.security.CustomLogoutHandler;
import bside.com.project308.security.security.CustomLogoutSuccessHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


@EnableWebSecurity(debug = true)
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final ObjectMapper objectMapper;
    private final CustomLoginFilter customLoginFilter;
    private final CustomJwtAuthorizationFilter jwtAuthorizationFilter;
    private final CustomJwtLoginFilter customJwtLoginFilter;
    private final CacheManager cacheManager;
    @Bean
    public SecurityFilterChain localFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrfConfigurer -> csrfConfigurer.disable());
        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.sessionManagement(sessionConfigurer -> sessionConfigurer.maximumSessions(1)
                                                                              .maxSessionsPreventsLogin(false));

        httpSecurity.requiresChannel(channel -> channel.anyRequest().requiresSecure());
        httpSecurity.authorizeHttpRequests(authConfigurer -> authConfigurer
                                    .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                                    //.requestMatchers(new AntPathRequestMatcher("/.well-known/pki-validation/**")).permitAll()
                                    //.requestMatchers(new AntPathRequestMatcher("/static/**")).permitAll()
                                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/avatar/**")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/member/skill"), new AntPathRequestMatcher("/member/default-img")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/member/sign-up"), new AntPathRequestMatcher("/member/skill", "GET")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/ex")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/**")).permitAll()
                                    .requestMatchers(new AntPathRequestMatcher("/login/oauth2/code/**")).permitAll()
                                    .anyRequest().authenticated()
                            )
                .addFilterAfter(customJwtLoginFilter, LogoutFilter.class)
                .addFilterBefore(jwtAuthorizationFilter, CustomJwtLoginFilter.class)
                .addFilterAfter(customLoginFilter, CustomJwtLoginFilter.class)
                .headers(headerConfigurer -> headerConfigurer.frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()))
                    .exceptionHandling(exConfigurer -> exConfigurer
                                   .authenticationEntryPoint(customAuthenticationEntrypoint())
                                    .accessDeniedHandler(customAccessDeniedHandler()))
                .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessHandler(customLogoutSuccessHandler())
                                                            .addLogoutHandler(customLogoutHandler()));

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
    @ConditionalOnProperty(prefix = "spring.config.activate", name = "on-profile", havingValue = "prod")
    public SecurityFilterChain prodFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(csrfConfigurer -> csrfConfigurer.disable());
        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.sessionManagement(sessionConfigurer -> sessionConfigurer.maximumSessions(1)
                                                                             .maxSessionsPreventsLogin(false));
        httpSecurity.requiresChannel(channel -> channel.anyRequest().requiresSecure());


        httpSecurity.authorizeHttpRequests(authConfigurer -> authConfigurer
                            .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.INCLUDE).permitAll()
                            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                            .requestMatchers(HttpMethod.GET, "/member/skill", "/member/default-img", "/avatar/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/member/sign-up").permitAll()
                            .requestMatchers(HttpMethod.GET, "/").permitAll()
                            .anyRequest().authenticated()
                    ).exceptionHandling(exConfigurer -> exConfigurer
                            .authenticationEntryPoint(customAuthenticationEntrypoint())
                            .accessDeniedHandler(customAccessDeniedHandler()));

        httpSecurity
                    .addFilterAfter(customJwtLoginFilter, LogoutFilter.class)
                    .addFilterBefore(jwtAuthorizationFilter, CustomJwtLoginFilter.class)
                    .addFilterAfter(customLoginFilter, CustomJwtLoginFilter.class)
                    .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessHandler(customLogoutSuccessHandler())
                                                                .addLogoutHandler(customLogoutHandler()));

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://coworker-matching.vercel.app"));
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowCredentials(true);
        //corsConfiguration.setAllowedHeaders(Arrays.asList("Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public LogoutSuccessHandler customLogoutSuccessHandler() {
        return new CustomLogoutSuccessHandler(objectMapper);
    }

    @Bean
    public CustomLogoutHandler customLogoutHandler() {
        return new CustomLogoutHandler(cacheManager);
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
