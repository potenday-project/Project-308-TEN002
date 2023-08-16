package bside.com.project308.member.dto;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.SkillCategory;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.entity.SkillMember;
import jdk.jfr.Category;

public record SkillDto(Long id,
                       String skillName,
                       Position position,
                       SkillCategory category) {

    public static SkillDto from(Skill skill) {
        return new SkillDto(skill.getId(), skill.getSkillName(), skill.getPosition(), skill.getSkillCategory());
    }
}
