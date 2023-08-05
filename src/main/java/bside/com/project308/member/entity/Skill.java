package bside.com.project308.member.entity;

import bside.com.project308.common.entity.BaseEntity;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.SkillCategory;
import jakarta.persistence.*;
import jdk.jfr.Category;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Skill extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_id")
    private Long id;
    private String skillName;
    @Enumerated(EnumType.STRING)
    private Position position;
    @Enumerated(EnumType.STRING)
    private SkillCategory skillCategory;

    private Skill(String skillName, Position position, SkillCategory skillCategory) {
        this.skillName = skillName;
        this.position = position;
        this.skillCategory = skillCategory;
    }

    public static Skill of(String skillName, Position position, SkillCategory skillCategory) {
        return new Skill(skillName, position, skillCategory);
    }
}
