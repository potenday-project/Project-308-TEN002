package bside.com.project308.security.filter;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.common.response.Response;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.security.CustomAuthenticationToken;
import bside.com.project308.security.security.UserPrincipal;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.StandardCharset;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.log.LogMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
@Slf4j
public class CustomLoginFilter extends OncePerRequestFilter {

    private final String LOGIN_URL = "/login";
    private final AntPathRequestMatcher DEFAULT_REQUEST_MATCHER = new AntPathRequestMatcher(LOGIN_URL, "POST");
    private SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    private final ObjectMapper objectMapper;
    private final MemberService memberService;
    private SessionAuthenticationStrategy sessionStrategy = new NullAuthenticatedSessionStrategy();
    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!DEFAULT_REQUEST_MATCHER.matches(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        String body = StreamUtils.copyToString(request.getInputStream(), StandardCharset.UTF_8);
        log.info("login request {}", body);
        MemberLoginRequest memberLoginRequest = objectMapper.readValue(body, MemberLoginRequest.class);
        log.info("login request {}", memberLoginRequest);
        try {
            requestValidationCheck(memberLoginRequest);
            MemberDto memberDto = memberService.getByUserProviderId(memberLoginRequest.user().providerUserId);
            UserPrincipal userPrincipal = UserPrincipal.of(memberDto.id(), memberDto.userProviderId(), memberDto.username(), memberDto.password());
            Authentication authentication = new CustomAuthenticationToken(userPrincipal, userPrincipal.getAuthorities());
            successfulAuthentication(request, response, filterChain, authentication);

        } catch (ResourceNotFoundException e) {
            HttpSession session = request.getSession();
            MemberDto memberDto = new MemberDto(null, memberLoginRequest.user.providerUserId, memberLoginRequest.user.nickname, null, null, RegistrationSource.KAKAO, null, memberLoginRequest.user.imgUrl, null, null);
            session.setAttribute("tempMemberDto", memberDto);
            unsuccessfulAuthentication(request, response);
        } catch (IllegalArgumentException e) {
            illegalAuthenticationRequest(request, response);
        }

    }

    private void requestValidationCheck(MemberLoginRequest memberLoginRequest) {
        if(memberLoginRequest == null ||
                memberLoginRequest.user == null ||
                !StringUtils.hasText(memberLoginRequest.user.providerUserId)){
            throw new IllegalArgumentException();
        }
    }


    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        HttpSession session = request.getSession();
        if(session.getAttribute("tempMemberDto") != null){
            session.removeAttribute("tempMemberDto");
        }

        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authResult);

        SecurityContextHolder.getContextHolderStrategy().setContext(context);
        this.securityContextRepository.saveContext(context, request, response);


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.OK.value());

        Response errorResponse = Response.success(ResponseCode.LOGIN_SUCCESS.getCode());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);

        //AuthenticationSuccessHandler authenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        //authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult);
    }

    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.securityContextHolderStrategy.clearContext();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.NOT_FOUND.value());

        Response errorResponse = Response.failResponse(ResponseCode.LOGIN_FAIL.getCode(), ResponseCode.LOGIN_FAIL.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }

    protected void illegalAuthenticationRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        this.securityContextHolderStrategy.clearContext();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.BAD_REQUEST.value());

        Response errorResponse = Response.failResponse(ResponseCode.BAD_LOGIN_ACCESS.getCode(), ResponseCode.BAD_LOGIN_ACCESS.getDesc());
        String body = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().println(body);
    }



    private static record MemberLoginRequest(User user,
                                             String expires,
                                             String accessToken
                ) {
    }

    public static record User(
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

