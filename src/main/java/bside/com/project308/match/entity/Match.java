package bside.com.project308.match.entity;

import bside.com.project308.common.entity.BaseEntity;
import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Objects;

/*
visit table과 분리한 이유?
Match 테이블은 상대적으로 정적인 반면 visit table은 count를 소진한 경우 전부 삭제하는 작업이 빈번할 수 있음
또한 message기능 고려했을 때 match table을 별도로 관리하는 것이 나아보임
*/
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "match_events")
public class Match extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member fromMember;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member toMember;

    //todo : 고민 필요
    @OneToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_match_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Match connectedMatch;
    private LocalDateTime matchTime;
    private Boolean checked;

    private Match(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
        this.matchTime = LocalDateTime.now();
        checked = Boolean.FALSE;
    }

    public static Match of(Member fromMember, Member toMember) {
        return new Match(fromMember, toMember);
    }

    public Member getMatchedTarget(Member member) {
        if (member.equals(fromMember)) {
            return toMember;
        } else {
            return fromMember;
        }
    }

    public void connectMatch(Match connectedMatch) {
        if (this.connectedMatch == null) {
            this.connectedMatch = connectedMatch;
        }
        if (connectedMatch.connectedMatch == null) {
            connectedMatch.connectedMatch = this;
        }
    }

    public Boolean isChecked() {
        return this.checked;
    }

    public void checkMatch() {
        this.checked = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return Objects.equals(id, match.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
