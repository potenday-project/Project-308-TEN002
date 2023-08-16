package bside.com.project308.member.repository;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findBySkillNameIn(List<String> skillNames);
    Set<Skill> findSetBySkillNameIn(List<String> skillNames);

    List<Skill> findByPosition(Position position);
}
