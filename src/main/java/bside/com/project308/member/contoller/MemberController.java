package bside.com.project308.member.contoller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.UnAuthorizedAccessException;
import bside.com.project308.common.response.Response;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.repository.SkillRepository;
import bside.com.project308.member.service.SkillService;
import bside.com.project308.security.security.UserPrincipal;
import bside.com.project308.member.dto.request.MemberUpdateRequest;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.member.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final SkillService skillService;

/*    @GetMapping
    public ResponseEntity<Response> getMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Response result = Response.success(ResponseCode.SUCCESS.getCode(),
                MemberResponse.from(memberService.getMemberInfo(userPrincipal.id())));
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }*/
    @GetMapping
    public ResponseEntity<Response> getMemberInfo(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberDto memberInfo = memberService.getMemberInfo(userPrincipal.id());
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), MemberResponse.from(memberInfo)));
    }


    @PutMapping
    public ResponseEntity<Response> updateMember(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                 @RequestBody MemberUpdateRequest memberUpdateRequest) {
        MemberDto updatedMember = memberService.update(userPrincipal.id(), memberUpdateRequest);

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.MEMBER_UPDATE_SUCCESS.getCode(), MemberResponse.from(updatedMember)));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Response> deleteMember(@PathVariable Long memberId,
                                                 @AuthenticationPrincipal UserPrincipal userPrincipal) {
        memberService.delete(memberId);
        Response result = Response.success(ResponseCode.SUCCESS.getCode());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Response> signUp(@RequestBody SignUpRequest signUpRequest,
                                           HttpSession httpSession) {
        if (httpSession == null || httpSession.getAttribute("tempMemberDto") == null) {
            throw new UnAuthorizedAccessException(ResponseCode.SIGN_UP_FAIL, "로그인이 필요합니다");
        }

        if (SecurityContextHolder.getContext().getAuthentication() != null && !(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
            throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.SIGN_UP_FAIL, "이미 회원가입된 사용자입니다");
        }
        MemberDto memberDto = memberService.singUp(signUpRequest, (MemberDto) httpSession.getAttribute("tempMemberDto"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(ResponseCode.SIGNUP_SUCCESS.getCode(), MemberResponse.from(memberDto)));
    }

    @GetMapping("/skill")
    public ResponseEntity<Response> getSkills(@RequestParam String position) {

        List<String> skill = skillService.getSkill(Position.valueOf(position));
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), skill));
    }




}
