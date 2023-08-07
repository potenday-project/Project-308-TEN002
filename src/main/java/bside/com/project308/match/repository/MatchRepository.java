package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Match;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MatchRepository extends JpaRepository<Match, Long> {
    Set<Match> findByFromMemberOrToMember(Member member1, Member member2);
    Set<Match> findByFromMember(Member fromMember);
    Optional<Match> findByFromMemberAndToMember(Member fromMember, Member toMember);

    List<Match> findByFromMemberOrderByMatchTimeDesc(Member fromMember);
    @EntityGraph(attributePaths = {"fromMember", "toMember"})
    List<Match> findByFromMemberAndCheckedFalse(Member fromMember);
    void deleteByFromMemberIdAndId(Long fromMemberId, Long matchId);
}
