package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Match;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MatchRepository extends JpaRepository<Match, Long> {
    @Query("select m from Match m join fetch m.fromMember join fetch m.toMember where (m.fromMember = :fromMember and m.toMember = :toMember) or (m.fromMember = :toMember and m.toMember = :fromMember)")
    Optional<Match> findMatchByMemberSet(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);
    Set<Match> findByFromMemberOrToMember(Member fromMember, Member toMember);
    @EntityGraph(attributePaths = {"fromMember", "toMember"})
    List<Match> findByFromMemberOrToMemberAndCheckedFalse(Member fromMember, Member toMember);
    @EntityGraph(attributePaths = {"fromMember", "toMember"})
    List<Match> findByFromMemberOrToMemberOrderByMatchTimeDesc(Member fromMember, Member toMember);


   /* Set<Match> findByFromMemberOrToMember(Member member1, Member member2);
    Set<Match> findByFromMemberOrToMember(@Param("loginMember") Member loginMember);
    @Query("select m from Match m where (m.fromMember = :fromMember and m.toMember = :toMember) or (m.fromMember = :toMember and m.toMember = :fromMember)")
    Optional<Match> findByFromMemberAndToMember(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);
    @Query("select m from Match m join fetch m.fromMember join fetch m.toMember where (m.fromMember = :loginMember or m.toMember = :loginMember) order by m.matchTime desc")
    List<Match> findByFromMemberOrderByMatchTimeDesc(@Param("loginMember")Member fromMember);

    void deleteByFromMemberIdAndId(Long fromMemberId, Long matchId);

    @EntityGraph(attributePaths = {"toMember"})
    Optional<Match> findByFromMemberIdAndIdAndToMemberId(Long fromMemberId, Long matchId, Long toMemberId);*/
}
