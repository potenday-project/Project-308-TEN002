package bside.com.project308.match.repository;

import bside.com.project308.match.entity.TodayMatch;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TodayMatchRepository extends JpaRepository<TodayMatch, Long> {
    List<TodayMatch> findByFromMember(Member fromMember);
    void deleteByFromMemberAndToMember(Member fromMember, Member toMember);

    @Modifying
    @Query("DELETE FROM TodayMatch")
    void resetTodayMatch();
}
