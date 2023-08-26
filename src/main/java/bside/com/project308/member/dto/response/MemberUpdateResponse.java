package bside.com.project308.member.dto.response;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.dto.InterestDto;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.member.dto.SkillDto;

import java.util.ArrayList;
import java.util.List;

public record MemberUpdateResponse(Long id,
                                   String userProviderId,
                                   String username,
                                   Position position,
                                   RegistrationSource registrationSource,
                                   String intro,
                                   String imgUrl,
                                   List<String> skill) {
    public static MemberUpdateResponse from(MemberDto member) {
        List<String> skill = new ArrayList<>();
        if (member.skill() != null) {
            skill = member.skill().stream().map(SkillDto::skillName).toList();
        }
        return new MemberUpdateResponse(member.id(),
                member.userProviderId(),
                member.username(),
                member.position(),
                member.registrationSource(),
                member.intro(),
                member.imgUrl(),
                skill);
    }
}
