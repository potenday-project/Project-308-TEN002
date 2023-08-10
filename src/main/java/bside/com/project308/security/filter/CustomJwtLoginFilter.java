package bside.com.project308.security.filter;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.security.jwt.JwtTokenProvider;
import bside.com.project308.security.security.UserPrincipal;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.JavaServiceLoadable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomJwtLoginFilter extends OncePerRequestFilter {
    private final String LOGIN_URL = "/login-jwt";
    private final AntPathRequestMatcher DEFAULT_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL, "POST");
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharset.UTF_8);
        log.info("login request body to jwt : {}", body);
        MemberLoginRequest memberLoginRequest =null;

        try {
            memberLoginRequest = objectMapper.readValue(body, MemberLoginRequest.class);
            log.info("login request object to jwt : {}", memberLoginRequest);
            requestValidationCheck(memberLoginRequest);
            MemberDto memberDto = memberService.getByUserProviderId(memberLoginRequest.user().providerUserId);
            UserPrincipal userPrincipal = UserPrincipal.of(memberDto.id(), memberDto.userProviderId(), memberDto.username(), memberDto.password(), null);

            String token = jwtTokenProvider.createToken(userPrincipal);
            successfulAuthentication(request, response, filterChain, token);
        } catch (IllegalArgumentException e) {
            illegalAuthenticationRequest(request, response);
        } catch (Exception e) {
            unsuccessfulAuthentication(request, response);
        }

    }

    private void requestValidationCheck(MemberLoginRequest memberLoginRequest) {
        if(memberLoginRequest == null ||
                memberLoginRequest.user == null ||
                !StringUtils.hasText(memberLoginRequest.user.providerUserId)){
            log.error("[Application Error] login request validation check error");
            throw new IllegalArgumentException();

        }
    }

    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            String token) throws IOException, ServletException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.setHeader(HttpHeaders.AUTHORIZATION, JwtTokenProvider.HEADER_PREFIX + token);



        Response successResponse = Response.success(ResponseCode.LOGIN_SUCCESS.getCode(), new Token(token));
        String body = objectMapper.writeValueAsString(successResponse);
        response.getWriter().println(body);

    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.error("[Application Error] login request fail!");
        this.securityContextHolderStrategy.clearContext();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Response errorResponse = Response.failResponse(ResponseCode.LOGIN_FAIL.getCode(), ResponseCode.LOGIN_FAIL.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }

    protected void illegalAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        log.error("[Application Error] login request fail!");
        this.securityContextHolderStrategy.clearContext();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Response errorResponse = Response.failResponse(ResponseCode.BAD_LOGIN_ACCESS.getCode(), ResponseCode.BAD_LOGIN_ACCESS.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }


    public record Token(String token) {

    }


    private record MemberLoginRequest(User user,
                                             String expires,
                                             String accessToken
    ) {
    }

    public record User(
            @JsonProperty(value = "id")
            @NotBlank
            String providerUserId,
            @JsonProperty(value = "name")
            String nickname,
            @JsonProperty(value = "email")
            String email,
            @JsonProperty(value = "image")
            String imgUrl
    ){

    }
}
