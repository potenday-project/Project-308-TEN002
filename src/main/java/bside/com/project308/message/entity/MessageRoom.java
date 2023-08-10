package bside.com.project308.message.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.match.entity.Match;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MessageRoom extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member fromMember;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member toMember;

    //match되지 않았는데 messaging을 할 여지가 있을까?
    //private Match match;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;
    private LocalDateTime createdTime;

    private MessageRoom(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.createdTime = LocalDateTime.now();
        this.lastMessage = null;
    }

    public static MessageRoom of(Member fromMember, Member toMember) {
        return new MessageRoom(fromMember, toMember);
    }

    public void updateLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageRoom that = (MessageRoom) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
