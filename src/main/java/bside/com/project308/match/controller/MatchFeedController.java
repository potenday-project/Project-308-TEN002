package bside.com.project308.match.controller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.request.MatchRequest;
import bside.com.project308.match.dto.response.LikeResponse;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.match.service.CountService;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.VisitService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchFeedController {

    private final MatchService matchService;
    private final VisitService visitService;
    private final CountService countService;
    @GetMapping("/feed")
    public ResponseEntity<Response> getMatchPartner(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        /*
        1. userPrincipal로 검색하면 match상대방이 한 명 나옴
        * */

        MemberDto matchPartner = matchService.getMatchPartner(userPrincipal.id());
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), MemberResponse.from(matchPartner)));

    }

    @PostMapping("/like")
    public ResponseEntity<Response> postLike(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MatchRequest matchRequest) {
        // visitService visit처리
        Integer count = countService.matchUserAndGetMatchCount(userPrincipal.id());
        Boolean isMatched = visitService.postLike(userPrincipal.id(), matchRequest.toMemberId(), matchRequest.like());
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.LIKE_POST_SUCCESS.getCode(), new LikeResponse(count, isMatched)));
    }

    @GetMapping("/unchecked-match")
    public ResponseEntity<Response> getMatchList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MatchDto> uncheckedMatchList = matchService.getUncheckedMatchList(userPrincipal.id());
        List<MatchResponse> matchResponses = uncheckedMatchList.stream().map(MatchResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), matchResponses));
    }

    //match result 가져오기
    //match 취소하기
    //match 확인하기
}
