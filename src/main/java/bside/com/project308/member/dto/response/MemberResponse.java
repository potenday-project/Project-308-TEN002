package bside.com.project308.member.dto.response;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.dto.InterestDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.SkillDto;

import java.util.List;

public record MemberResponse(Long id,
                             String userProviderId,
                             String username,
                             Position position,
                             RegistrationSource registrationSource,
                             String intro,
                             String imgUrl,
                             List<String> interest,
                             List<String> skill) {


    public static MemberResponse from(MemberDto member) {
        return new MemberResponse(member.id(),
                member.userProviderId(),
                member.username(),
                member.position(),
                member.registrationSource(),
                member.intro(),
                member.imgUrl(),
                member.interest().stream().map(InterestDto::interest).toList(),
                member.skill().stream().map(SkillDto::skillName).toList());
    }
}
