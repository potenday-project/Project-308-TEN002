package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Count;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CountRepository extends JpaRepository<Count, Long> {
    Optional<Count> findByMember(Member member);

    @Modifying
    @Query("DELETE FROM Count")
    void resetCount();
}
