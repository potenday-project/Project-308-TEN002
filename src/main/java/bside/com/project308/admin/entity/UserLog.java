package bside.com.project308.admin.entity;

import bside.com.project308.admin.Type;
import bside.com.project308.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserLog extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_log_id")
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Type type;

    public UserLog(Long userId, Type type) {
        this.userId = userId;
        this.type = type;
    }
}
