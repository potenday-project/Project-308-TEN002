package bside.com.project308.match.controller.usecase;

import bside.com.project308.match.service.CountService;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GetTodayMatchPartnerList {
    private final MatchService matchService;
    private final MemberService memberService;
    private final CountService countService;
    @Transactional
    public List<MemberDto> execute(Long memberId){
        Member member = memberService.getMemberById(memberId);
        if(countService.getSwipeCount(member).isExhausted()){
            return Collections.emptyList();
        }

        List<MemberDto> todayMatchPartnerList = matchService.getTodayMatchPartnerList(member);
        if (todayMatchPartnerList.size() > 5) {
            todayMatchPartnerList = todayMatchPartnerList.subList(0, 5);
        }
        return todayMatchPartnerList;
    }
}
