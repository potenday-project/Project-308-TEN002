package bside.com.project308.member.entity;

import bside.com.project308.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Interest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long id;

    private String interest;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;



    private Interest(String interest, Member member) {
        this.interest = interest;
        this.member = member;
    }

    public static Interest of(String interest, Member member) {
        return new Interest(interest, member);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interest interest = (Interest) o;
        return Objects.equals(id, interest.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
