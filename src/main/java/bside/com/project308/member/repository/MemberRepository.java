package bside.com.project308.member.repository;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserProviderId(String userProviderId);
    Set<Member> findTop50ByIdGreaterThanAndPositionInAndIdNot(Long lastVisitedId, List<Position> positions, Long memberId);
    Set<Member> findSetByPositionInAndIdNot(List<Position> positions, Long memberId);
    List<Member> findInitialMemberByIdIn(List<Long> memberIds);
    List<Member> findInitialMemberProByUserProviderIdIn(Set<String> memberIds);
    List<Member> findInitialMemberProByUserProviderIdInAndPositionIn(Set<String> memberIds, List<Position> positions);

    Optional<Member> findInitialMemberByUserProviderId(String memberId);
}
