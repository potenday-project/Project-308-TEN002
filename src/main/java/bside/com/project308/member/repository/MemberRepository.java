package bside.com.project308.member.repository;

import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserProviderId(String userProviderId);
}
