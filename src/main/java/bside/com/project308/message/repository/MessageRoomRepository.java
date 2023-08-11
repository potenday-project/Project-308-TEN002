package bside.com.project308.message.repository;

import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {
    @Query("select mr from MessageRoom mr where (mr.fromMember = :fromMember and mr.toMember = :toMember)" +
            "or (mr.fromMember = :toMember and mr.toMember = :fromMember)")
    Optional<MessageRoom> findByFromMemberAndToMember(@Param("fromMember") Member fromMember, @Param("toMember") Member toMember);
    List<MessageRoom> findByFromMemberOrToMember(Member fromMember, Member toMember);

    void deleteByFromMemberAndToMember(Member fromMember, Member toMember);
}
