package bside.com.project308.message.repository;

import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.MessageRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageRoomRepository extends JpaRepository<MessageRoom, Long> {
    Optional<MessageRoom> findByFromMemberAndToMember(Member fromMember, Member toMember);
}
