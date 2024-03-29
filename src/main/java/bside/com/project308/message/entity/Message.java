package bside.com.project308.message.entity;

import bside.com.project308.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
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
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "message_room_id", nullable = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private MessageRoom messageRoom;
    private boolean isFromMemberMessage;
    private boolean isRead;
    private LocalDateTime messageCreatedTime;

    private Message(String content, MessageRoom messageRoom, boolean isFromMemberMessage, boolean isRead) {
        this.content = content;
        this.messageRoom = messageRoom;
        this.isFromMemberMessage = isFromMemberMessage;
        this.isRead = isRead;
        this.messageCreatedTime = LocalDateTime.now();
    }

    //생성 즉시 상대방이 message는 읽을 수 없으므로 iseRead는 false
    public static Message of(String content, MessageRoom messageRoom, boolean isFromMemberMessage) {
        return new Message(content, messageRoom, isFromMemberMessage, false);
    }

    public void readMessage() {
        this.isRead = true;
    }

    public static Message getDefaultMessage(MessageRoom messageRoom, boolean isFromMemberMessage) {
        return new Message("새로운 매칭이 시작되었습니다.", messageRoom, isFromMemberMessage, false);
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
