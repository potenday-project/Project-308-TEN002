package bside.com.project308.match.entity;

import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "visit_id")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    private Member fromMember;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    private Member toMember;
    private Boolean matchResult;

    private Visit(Member fromMember, Member toMember, Boolean matchResult) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.matchResult = matchResult;
    }

    public static Visit of(Member fromMember, Member toMember, Boolean like) {
        return new Visit(fromMember, toMember, like);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Visit visit = (Visit) o;
        return Objects.equals(id, visit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
