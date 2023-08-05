package bside.com.project308.member.repository;

import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.entity.SkillMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SkillMemberRepository extends JpaRepository<SkillMember, Long> {
    @Query("SELECT sm FROM SkillMember sm JOIN FETCH sm.skill WHERE sm.member = :member")
    List<SkillMember> findSkillByMember(Member member);
}
