package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Count;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountRepository extends JpaRepository<Count, Long> {
    Optional<Count> findByMember(Member member);
}
