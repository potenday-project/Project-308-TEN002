package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Match;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Set<Match> findByFromMemberOrToMember(Member member1, Member member2);
    Set<Match> findByFromMember(Member fromMember);
    @EntityGraph(attributePaths = {"fromMember", "toMember"})
    List<Match> findByFromMemberAndCheckedFalse(Member fromMember);
}
