package bside.com.project308.match.algorithm;

import bside.com.project308.common.constant.MemberGrade;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.entity.Member;

import java.util.Collection;
import java.util.List;

public interface MatchAlgorithm {

    List<Member> getTodayMatchPartner(Member member);
    boolean support(MemberGrade memberGrade);
}
