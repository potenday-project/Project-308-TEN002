package bside.com.project308.match.repository;

import bside.com.project308.match.entity.Swipe;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {
    Set<Swipe> findByFromMember(Member member);

    Optional<Swipe> findByFromMemberAndToMember(Member FromMember, Member toMember);
    void deleteByFromMember(Member fromMember);
}
