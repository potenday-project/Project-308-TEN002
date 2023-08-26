package bside.com.project308.member.dto;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.SkillMember;

import java.util.List;
import java.util.Objects;

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

    public static MemberDto from(Member member, List<SkillDto> skill) {
        return new MemberDto(member.getId(),
                member.getUserProviderId(),
                member.getUsername(),
                member.getPassword(),
                member.getPosition(),
                member.getRegistrationSource(),
                member.getIntro(),
                member.getImgUrl(),
                null,
                skill);
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MemberDto memberDto = (MemberDto) o;
        return Objects.equals(id, memberDto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
