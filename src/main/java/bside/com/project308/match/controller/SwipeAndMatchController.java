package bside.com.project308.match.controller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.response.Response;
import bside.com.project308.match.controller.usecase.GetTodayMatchPartnerList;
import bside.com.project308.match.controller.usecase.SwipeAndCheckMatch;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.request.MatchRequest;
import bside.com.project308.match.dto.response.SwipeResponse;
import bside.com.project308.match.dto.response.MatchMemberResponse;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.match.service.CountService;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.notification.service.MatchNotificationService;
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
public class SwipeAndMatchController {

    private final MatchService matchService;
    private final GetTodayMatchPartnerList getTodayMatchPartnerList;
    private final SwipeAndCheckMatch swipeAndCheckMatch;
    private final MatchNotificationService matchNotificationService;

    @GetMapping("/today-list")
    public ResponseEntity<Response> getMatchTodayPartner(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MemberDto> matchPartners = getTodayMatchPartnerList.execute(userPrincipal.id());
        List<MemberResponse> matchPartnerResponses = matchPartners.stream().map(MemberResponse::from).toList();

        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), matchPartnerResponses));

    }

    @PostMapping("/like")
    public ResponseEntity<Response> swipe(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MatchRequest matchRequest) {

        // 비정상적 match요청을 검증하기 위한 process
        if (userPrincipal.id() == matchRequest.toMemberId()) {
            throw new InvalidAccessException(HttpStatus.OK, ResponseCode.BAD_REQUEST);
        }

        SwipeResponse swipeResponse = swipeAndCheckMatch.execute(userPrincipal.id(), matchRequest);
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.LIKE_POST_SUCCESS.getCode(), swipeResponse));
    }

    @GetMapping("/unchecked-match")
    public ResponseEntity<Response> getMatchList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MatchDto> uncheckedMatchList = matchService.getUncheckedMatchList(userPrincipal.id());
        List<MatchResponse> matchResponses = uncheckedMatchList.stream().map(MatchResponse::from).toList();

        return ResponseEntity.status(HttpStatus.OK)
                             .body(Response.success(ResponseCode.SUCCESS.getCode(), matchResponses));
    }


    @GetMapping("/all")
    public ResponseEntity<Response> getAllMatchList(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<MatchDto> allMatchList = matchService.getAllMatchList(userPrincipal.id());
        List<MatchResponse> matchResponses = allMatchList.stream().map(MatchResponse::from).toList();
        return ResponseEntity.status(HttpStatus.OK)
                             .body(Response.success(ResponseCode.SUCCESS.getCode(), matchResponses));
    }

    @DeleteMapping("/{matchId}")
    public ResponseEntity<Response> deleteMatch(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long matchId) {
        matchService.deleteMatch(userPrincipal.id(), matchId);
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.MATCH_DELETE_SUCCESS.getCode()));

    }

    @PutMapping("/{matchId}")
    public ResponseEntity<Response> checkMatch(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                               @PathVariable Long matchId) {
        matchService.checkMatch(userPrincipal.id(), matchId);
        matchNotificationService.checkMatch(userPrincipal.id(), matchId);
        return ResponseEntity.ok(Response.success(ResponseCode.SUCCESS.getCode()));
    }

    @GetMapping("/{matchId}/member/{matchedMemberId}")
    public ResponseEntity<Response> getMemberInfo(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                  @PathVariable Long matchId,
                                                  @PathVariable Long matchedMemberId) {

        MemberDto matchedMemberInfo = matchService.getMatchedMemberInfo(userPrincipal.id(), matchId, matchedMemberId);
        MatchMemberResponse matchMemberResponse = new MatchMemberResponse(matchId, userPrincipal.id(), MemberResponse.from(matchedMemberInfo));
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), matchMemberResponse));

    }
}
