package bside.com.project308.member.repository;

import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findByMember(Member member);
    Set<Interest> findSetByMember(Member member);
}
