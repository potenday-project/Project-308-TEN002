package bside.com.project308.member;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.constant.SkillCategory;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.repository.SkillMemberRepository;
import bside.com.project308.member.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SetUp {

    private final MemberRepository memberRepository;
    private final SkillMemberRepository skillMemberRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;

    @PostConstruct
    @Transactional
    public void init() {
        Member member = Member.builder().userProviderId("2947153334")
                              .username("쫑")
                              .password("ddd")
                              .registrationSource(RegistrationSource.KAKAO)
                              .position(Position.BACK_END)
                              .build();
        memberRepository.save(member);

        Skill skill = Skill.of("Spring", Position.BACK_END, SkillCategory.FRAME_WORK);
        Skill skill2 = Skill.of("MySql", Position.BACK_END, SkillCategory.FRAME_WORK);
        skillRepository.save(skill);
        skillRepository.save(skill2);

    }
}
