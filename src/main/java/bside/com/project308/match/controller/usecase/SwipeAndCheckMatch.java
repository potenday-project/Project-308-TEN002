package bside.com.project308.match.controller.usecase;

import bside.com.project308.match.dto.request.MatchRequest;
import bside.com.project308.match.dto.response.SwipeResponse;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.service.CountService;
import bside.com.project308.match.service.SwipeService;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import bside.com.project308.message.dto.MessageRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SwipeAndCheckMatch {
    private final MemberService memberService;
    private final CountService countService;
    private final SwipeService swipeService;
    private final TodayMatchRepository todayMatchRepository;
    private final MakeMatchAndMessageRoom makeMatchAndMessageRoom;

    @Transactional
    public SwipeResponse execute(Long memberId, MatchRequest matchRequest) {
        Member fromMember = memberService.getMemberById(memberId);
        Integer countAfterSwipe = countService.useSwipeAndGetCount(fromMember).getCurCount();
        Member toMember = memberService.getMemberById(matchRequest.toMemberId());
        todayMatchRepository.deleteByFromMemberAndToMember(fromMember, toMember);

        boolean isMatched = swipeService.swipe(fromMember, toMember, matchRequest.like());
        if (isMatched) {
            MessageRoomDto messageRoomDto = makeMatchAndMessageRoom.execute(fromMember, toMember);
        }

        return new SwipeResponse(countAfterSwipe);

    }


}
