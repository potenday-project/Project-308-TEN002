package bside.com.project308.match.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TodayMatch extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "today_match_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member fromMember;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member toMember;

    private TodayMatch(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
    }

    public static TodayMatch of(Member fromMember, Member toMember) {
        return new TodayMatch(fromMember, toMember);
    }
}
