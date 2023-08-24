package bside.com.project308.member.contoller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.contoller.usecase.SingUpMemberAndSetDefaultMatch;
import bside.com.project308.member.dto.response.ImgResponse;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.request.MemberUpdateRequest;
import bside.com.project308.member.dto.request.SignUpRequest;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.member.service.SkillService;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final SkillService skillService;
    private final SingUpMemberAndSetDefaultMatch singUpMemberAndSetDefaultMatch;


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

    @DeleteMapping
    public ResponseEntity<Response> deleteMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        memberService.delete(userPrincipal.id());
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.MEMBER_DELETE_SUCCESS.getCode()));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Response> signUp(@Validated @RequestBody SignUpRequest signUpRequest) {

        MemberDto createdMember = singUpMemberAndSetDefaultMatch.execute(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success(ResponseCode.SIGNUP_SUCCESS.getCode(), MemberResponse.from(createdMember)));
    }

    @GetMapping("/skill")
    public ResponseEntity<Response> getSkills(@RequestParam String position) {

        List<String> skill = skillService.getSkill(Position.valueOf(position));
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), skill));
    }

    @GetMapping("/default-img")
    public ResponseEntity<Response> getDefaultImgs() {

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), ImgResponse.imgs));
    }




}
