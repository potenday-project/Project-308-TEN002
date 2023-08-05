package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Visit;
import bside.com.project308.match.entity.VisitedMemberCursor;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    Set<Visit> findByFromMember(Member member);

    Optional<Visit> findByFromMemberAndToMember(Member FromMember, Member toMember);

}
