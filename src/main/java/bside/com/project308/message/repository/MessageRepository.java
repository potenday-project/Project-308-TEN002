package bside.com.project308.message.repository;

import bside.com.project308.member.entity.Member;
import bside.com.project308.message.entity.Message;
import bside.com.project308.message.entity.MessageRoom;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    boolean existsByIsFromMemberMessageAndIsReadFalseAndMessageRoom(boolean isFromMember, MessageRoom messageRoom);
    List<Message> findByMessageRoom(MessageRoom messageRoom);
}
