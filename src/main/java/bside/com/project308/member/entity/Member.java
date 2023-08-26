package bside.com.project308.member.entity;

import bside.com.project308.common.entity.BaseEntity;
import bside.com.project308.common.entity.BaseTimeEntity;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id", updatable = false)
    private Long id;
    private String userProviderId;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Position position;
    @Enumerated(EnumType.STRING)
    private RegistrationSource registrationSource;
    @Column(columnDefinition = "text")
    private String intro;
    private String imgUrl;
    private LocalDateTime lastLogin;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    List<Interest> interests = new ArrayList<>();
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", orphanRemoval = true, cascade = CascadeType.ALL)
    List<SkillMember> skills = new ArrayList<>();

    @Builder
    public Member(String userProviderId, String username, String password, Position position, RegistrationSource registrationSource, String intro, String imgUrl) {
        this.userProviderId = userProviderId;
        this.username = username;
        this.password = password;
        this.position = position;
        this.registrationSource = registrationSource;
        this.intro = intro;
        this.imgUrl = imgUrl;
    }

    @Builder(builderMethodName = "fullBuild")
    public Member(String userProviderId, String username, String password, Position position, RegistrationSource registrationSource,
                  String intro, String imgUrl, List<Interest> interests, List<SkillMember> skills) {
        this.userProviderId = userProviderId;
        this.username = username;
        this.password = password;
        this.position = position;
        this.registrationSource = registrationSource;
        this.intro = intro;
        this.imgUrl = imgUrl;
        this.interests = interests;
        this.skills = skills;
    }

    public void updateMembeInfo(String username, String intro, List<SkillMember> skills) {
        this.username = username == null ? this.username : username;
        this.intro = intro == null ? this.intro : intro;
        this.skills.clear();
        this.skills.addAll(skills);
    }

    public void updateMembeInfo(String username, String intro) {
        this.username = username == null ? this.username : username;
        this.intro = intro == null ? this.intro : intro;
    }

    public void updateSkillAndInterests(List<Interest> interests, List<SkillMember> skills) {
        this.interests.clear();
        this.interests.addAll(interests);
        this.skills.clear();
        this.skills.addAll(skills);
    }

    public void updateInterests(List<Interest> interests) {
        this.interests.clear();
        this.interests.addAll(interests);
    }

    public void updateLastLoginTime() {
        this.lastLogin = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return Objects.equals(id, member.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
