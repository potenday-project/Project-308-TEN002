package bside.com.project308.match.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class VisitedMemberCursor extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cursor_id")
    private Long id;
    @OneToOne(optional = false)
    @JoinColumn(name = "member_id")
    private Member member;
    @Setter private Long lastVisitedMemberId;

    public VisitedMemberCursor(Member member) {
        this.member = member;
        this.lastVisitedMemberId = 1L;
    }

    public VisitedMemberCursor(Member member, Long lastVisitedMemberId) {
        this.member = member;
        this.lastVisitedMemberId = lastVisitedMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitedMemberCursor that = (VisitedMemberCursor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
