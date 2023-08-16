package bside.com.project308.match.repository;

import bside.com.project308.match.entity.VisitedMemberCursor;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VisitedMemberCursorRepository extends JpaRepository<VisitedMemberCursor, Long> {
    Optional<VisitedMemberCursor> findByMember(Member member);
}
