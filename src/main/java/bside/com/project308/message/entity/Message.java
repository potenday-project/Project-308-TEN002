package bside.com.project308.message.entity;

import bside.com.project308.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name= "message_id")
    private Long id;
    @Lob
    private String content;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_room_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MessageRoom messageRoom;
    private boolean isFromMemberMessage;
    private boolean isRead;

    private Message(String content, MessageRoom messageRoom, boolean isFromMemberMessage, boolean isRead) {
        this.content = content;
        this.messageRoom = messageRoom;
        this.isFromMemberMessage = isFromMemberMessage;
        this.isRead = isRead;
    }

    //생성 즉시 상대방이 message는 읽을 수 없으므로 iseRead는 false
    public static Message of(String content, MessageRoom messageRoom, boolean isFromMemberMessage) {
        return new Message(content, messageRoom, isFromMemberMessage, false);
    }

    public void readMessage() {
        this.isRead = true;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(id, message.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
