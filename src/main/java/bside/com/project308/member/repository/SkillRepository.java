package bside.com.project308.member.repository;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findBySkillNameIn(List<String> skillNames);

    List<Skill> findByPosition(Position position);
}
