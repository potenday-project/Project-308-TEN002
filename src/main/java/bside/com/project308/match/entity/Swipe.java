package bside.com.project308.match.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Swipe extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "swipe_id")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member fromMember;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member toMember;
    private Boolean isLike;

    private Swipe(Member fromMember, Member toMember, Boolean isLike) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.isLike = isLike;
    }

    public static Swipe of(Member fromMember, Member toMember, Boolean like) {
        return new Swipe(fromMember, toMember, like);
    }

    public void updateLike(Boolean like) {
        this.isLike = like;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Swipe visit = (Swipe) o;
        return Objects.equals(id, visit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
