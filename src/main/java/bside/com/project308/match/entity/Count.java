package bside.com.project308.match.entity;

import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Count extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "count_id")
    private Long id;
    @OneToOne
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    private Integer maxCount;
    private Integer curCount;
    private LocalDateTime maxExhaustionTime;
    private Boolean exhausted;

    private Count(Member member, Integer curCount, Boolean exhausted) {
        this.maxCount = 1000;
        this.member = member;
        this.curCount = curCount;
        this.exhausted = exhausted;
    }
    public static Count of(Member member) {
        return new Count(member, 0, false);
    }

    public Boolean isExhausted() {
        return exhausted;
    }

    public Integer useMatch() {
        Assert.state(!this.exhausted, "모든 매치 횟수를 소진하였습니다.");
        this.curCount++;
        if (curCount == this.maxCount) {
            this.exhausted = true;
        }
        return this.curCount;
    }

    public void changeMaxCount(int maxCount) {
        Assert.state(maxCount >= 5, "최소 매치 횟수는 5회입니다.");
        this.maxCount = maxCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Count count = (Count) o;
        return Objects.equals(id, count.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
