package bside.com.project308.match.controller;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.response.Response;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.request.MatchRequest;
import bside.com.project308.match.dto.response.LikeResponse;
import bside.com.project308.match.dto.response.MatchMemberResponse;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.match.service.CountService;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.response.MemberResponse;
import bside.com.project308.message.dto.MessageRoomDto;
import bside.com.project308.message.service.MessageRoomService;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("/match")
public class MatchFeedController {

    private final MatchService matchService;
    private final SwipeService swipeService;
    private final CountService countService;
    private final MessageRoomService messageRoomService;

    /*@GetMapping("/feed")
    public ResponseEntity<Response> getMatchPartner(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberDto matchPartner = matchService.getMatchPartner(userPrincipal.id());
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), MemberResponse.from(matchPartner)));

    }*/

    @GetMapping("/today-list")
    public ResponseEntity<Response> getMatchTodayPartner(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        /*
        1. userPrincipal로 검색하면 match상대방이 한 명 나옴
        * */

        try{
            countService.getMatchCount(userPrincipal.id());
        }catch (InvalidAccessException e){
            return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), Collections.emptyList()));

        }

        List<MemberDto> matchPartners = matchService.getTodayMatchPartner(userPrincipal.id());
        List<MemberResponse> matchPartnerResponses = matchPartners.stream().map(MemberResponse::from).toList();
        if (matchPartnerResponses.size() > 5) {
            matchPartnerResponses = matchPartnerResponses.subList(0, 5);
        }
        return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.SUCCESS.getCode(), matchPartnerResponses));

    }

    @PostMapping("/like")
    public ResponseEntity<Response> postLike(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody MatchRequest matchRequest) {

        // 비정상적 match요청을 검증하기 위한 process
        if (userPrincipal.id() == matchRequest.toMemberId()) {
            throw new InvalidAccessException(HttpStatus.OK, ResponseCode.BAD_REQUEST);
        }

        //count를 소진한 경우에는 exception이 발생함

        Integer usedCount = countService.matchUserAndGetMatchCount(userPrincipal.id());



        Optional<MatchDto> matchDto = swipeService.postLike(userPrincipal.id(), matchRequest.toMemberId(), matchRequest.like());

        if (matchDto.isPresent()) {
            MessageRoomDto messageRoom = messageRoomService.getMessageRoom(matchDto.get().id());
            MatchResponse matchResponse = MatchResponse.from(matchDto.get());
            return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.LIKE_POST_SUCCESS.getCode(), new LikeResponse(usedCount, true, matchResponse, messageRoom.id())));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(Response.success(ResponseCode.LIKE_POST_SUCCESS.getCode(), new LikeResponse(usedCount, false, null, null)));
        }
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
