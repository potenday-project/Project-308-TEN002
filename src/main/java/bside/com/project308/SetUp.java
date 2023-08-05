package bside.com.project308;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.constant.SkillCategory;
import bside.com.project308.member.entity.Interest;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.entity.Skill;
import bside.com.project308.member.entity.SkillMember;
import bside.com.project308.member.repository.InterestRepository;
import bside.com.project308.member.repository.MemberRepository;
import bside.com.project308.member.repository.SkillMemberRepository;
import bside.com.project308.member.repository.SkillRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
        List<Member> members = new ArrayList<>();
        List<Interest> interests = new ArrayList<>();
        List<Skill> skills = new ArrayList<>();
        List<SkillMember> skillMembers = new ArrayList<>();
        Position[] values = Position.values();

        for (int i = 0; i < 10; i++) {
            Position position = values[i % 4];
            Skill skill = Skill.of("Python" + i, position, SkillCategory.FRAME_WORK);
            skills.add(skill);
        }
        skillRepository.saveAll(skills);
        long start = 2000000000L;
        long end = 3000000000L;
        long range = end - start + 1;
        Random generator = new Random();

        for (int i = 0; i < 100; i++) {
            long id = (long)(generator.nextDouble() * range + start);
            Member member = Member.builder()
                                  .userProviderId(String.valueOf(id))
                    .username("user" + i)
                    .password("ddd")
                    .registrationSource(RegistrationSource.KAKAO)
                    .position(values[i % 4])
                    .intro("안녕하세요")
                    .imgUrl("aaaa.jpg")
                    .build();
            members.add(member);
            interests.addAll(Arrays.asList(new Interest[]{Interest.of(values[(i + 1) % 4].toString(), member), Interest.of(values[(i + 2) % 4].toString(), member)}));
            List<Skill> selectedSkill = skills.stream().filter(skill -> skill.getPosition() == member.getPosition()).toList();
            List<SkillMember> skillMemberTable = selectedSkill.stream().map(skill -> SkillMember.of(skill, member)).toList();
            skillMembers.addAll(skillMemberTable);

        }


        memberRepository.saveAll(members);
        interestRepository.saveAll(interests);
        skillMemberRepository.saveAll(skillMembers);



    }
}
