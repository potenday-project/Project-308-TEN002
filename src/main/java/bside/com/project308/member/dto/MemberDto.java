package bside.com.project308.member.dto;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.entity.Member;

import java.util.List;

public record MemberDto(Long id,
                        String userProviderId,
                        String username,
                        String password,
                        Position position,
                        RegistrationSource registrationSource,
                        String intro,
                        String imgUrl,
                        List<InterestDto> interest,
                        List<SkillDto> skill) {

    public static MemberDto from(Member member) {
        return new MemberDto(member.getId(),
                member.getUserProviderId(),
                member.getUsername(),
                member.getPassword(),
                member.getPosition(),
                member.getRegistrationSource(),
                member.getIntro(),
                member.getImgUrl(),
                null,
                null);
    }

    public static MemberDto from(Member member, List<InterestDto> interest, List<SkillDto> skill) {
        return new MemberDto(member.getId(),
                member.getUserProviderId(),
                member.getUsername(),
                member.getPassword(),
                member.getPosition(),
                member.getRegistrationSource(),
                member.getIntro(),
                member.getImgUrl(),
                interest,
                skill);
    }


}
