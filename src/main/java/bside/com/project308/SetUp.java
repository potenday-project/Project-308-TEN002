package bside.com.project308;

import bside.com.project308.match.entity.Count;
import bside.com.project308.match.entity.Match;
import bside.com.project308.match.entity.Visit;
import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.VisitService;
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
import bside.com.project308.message.dto.MessageRoomWithNewMessageCheck;
import bside.com.project308.message.dto.request.MessageRequest;
import bside.com.project308.message.service.MessageRoomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SetUp {

    private final MemberRepository memberRepository;
    private final SkillMemberRepository skillMemberRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;
    private final VisitRepository visitRepository;
    private final MatchRepository matchRepository;
    private final PlatformTransactionManager txManager;
    private final MatchService matchService;
    private final VisitService visitService;
    private final MessageRoomService messageRoomService;
    private final CountRepository countRepository;
    public static List<Member> members = new ArrayList<>();
    @PostConstruct
    @Transactional
    public void init() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                //List<Member> members = new ArrayList<>();
                List<Interest> interests = new ArrayList<>();
                List<Skill> skills = skillSetup();
                List<SkillMember> skillMembers = new ArrayList<>();
                Position[] values = Position.values();



                skillRepository.saveAll(skills);
                long start = 2000000000L;
                long end = 3000000000L;
                long range = end - start + 1;
                Random generator = new Random();

                for (int i = 0; i < 100; i++) {
                    //long id = (long)(generator.nextDouble() * range + start);
                    Member member = Member.builder()
                                          .userProviderId(String.valueOf(1000 + i))
                                          .username("user" + i)
                                          .password("ddd")
                                          .registrationSource(RegistrationSource.KAKAO)
                                          .position(values[i % 4])
                                          .intro("안녕하세요")
                                          .imgUrl("https://i.pravatar.cc/150?u=fake@pravatar.com")
                                          .build();
                    members.add(member);
                    interests.addAll(Arrays.asList(new Interest[]{Interest.of(values[(i + 1) % 4].toString(), member), Interest.of(values[(i + 2) % 4].toString(), member)}));

                    List<Skill> selectedSkill = skills.stream()
                                                      .filter(skill -> skill.getPosition() == member.getPosition())
                                                      .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                                                      .toList().subList(0, 4);

                    List<SkillMember> skillMemberTable = selectedSkill.stream().map(skill -> SkillMember.of(skill, member)).toList();
                    skillMembers.addAll(skillMemberTable);

                }
                memberRepository.saveAll(members);
                interestRepository.saveAll(interests);
                skillMemberRepository.saveAll(skillMembers);


                //1번 멤버는 모든 사람과 매칭됨
                List<Match> matches = new ArrayList<>();
                for (int i = 1; i < members.size(); i++) {
                    matchService.match(members.get(0), members.get(i));
                }

                List<Visit> visits1 = new ArrayList<>();

                //2번 멤버는 1번 외 모든 사용자에 대해 좋아요를 눌러놓음
                for (int i = 2; i < members.size(); i++) {
                    Visit visit = Visit.of(members.get(1), members.get(i), true);
                    visits1.add(visit);

                }

                visitRepository.saveAll(visits1);
                List<Visit> visits2 = new ArrayList<>();
                //3 ~ 4멤버는 자기보다 10번째 더 많은 사람까지 좋아요를 눌러놓음
                for (int i = 3; i < 5; i++) {
                    for (int j = i + 1; j < i + 10; j++) {
                        Visit visit = Visit.of(members.get(i), members.get(j), true);
                        visits2.add(visit);
                    }
                }

                visitRepository.saveAll(visits2);

                Member customMember = Member.builder()
                                      .userProviderId("2947153334")
                                      .username("박지혜")
                                      .password("ddd")
                                      .registrationSource(RegistrationSource.KAKAO)
                                      .position(Position.FRONT_END)
                                      .intro("안녕하세요")
                                      .imgUrl("https://i.pravatar.cc/150?u=fake@pravatar.com")
                                      .build();

                List<Interest> customIterests = Arrays.asList(Interest.of("BACK_END", customMember),
                        Interest.of("FRONT_END", customMember),
                        Interest.of("DESIGNER", customMember),
                        Interest.of("PM_PO", customMember)

                );

                List<Skill> customSkilll = skills.stream()
                                                  .filter(skill -> skill.getPosition() == customMember.getPosition())
                                                  .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                                                  .toList().subList(0, 4);

                List<SkillMember> skillMemberTable = customSkilll.stream().map(skill -> SkillMember.of(skill, customMember)).toList();
                memberRepository.save(customMember);
                interestRepository.saveAll(customIterests);
                skillMemberRepository.saveAll(skillMemberTable);
                visitService.postLike(members.get(0).getId(), customMember.getId(), true);
                IntStream.rangeClosed(1, 40)
                                 .forEach(i -> visitService.postLike(members.get(i).getId(), customMember.getId(), true));




                matchService.match(customMember, members.get(3));
                matchService.match(customMember, members.get(5));
                matchService.match(customMember, members.get(12));


                List<MessageRoomWithNewMessageCheck> allMessageRoomList = messageRoomService.getAllMessageRoomList(customMember.getId());
                MessageRequest messageRequest1 = new MessageRequest(allMessageRoomList.get(0).id(), "지혜님 테스트 메시지입니다");
                MessageRequest messageRequest2 = new MessageRequest(allMessageRoomList.get(1).id(), "지혜님 테스트 메시지입니다");
                MessageRequest messageRequest3 = new MessageRequest(allMessageRoomList.get(2).id(), "지혜님 테스트 메시지입니다");
                messageRoomService.writeMessage(customMember.getId(), messageRequest1);
                messageRoomService.writeMessage(customMember.getId(), messageRequest2);
                messageRoomService.writeMessage(customMember.getId(), messageRequest3);

                List<MessageRoomWithNewMessageCheck> allMessageRoomList2 = messageRoomService.getAllMessageRoomList(members.get(12).getId());
                MessageRoomWithNewMessageCheck messageRoomWithNewMessageCheck2 = allMessageRoomList2.get(1);

                MessageRequest messageRequest4 = new MessageRequest(messageRoomWithNewMessageCheck2.id(), "상대방 테스트 메시지입니다");
                messageRoomService.writeMessage(members.get(12).getId(), messageRequest4);
                messageRoomService.writeMessage(members.get(12).getId(), messageRequest4);

                messageRoomService.writeMessage(customMember.getId(), messageRequest1);
                messageRoomService.writeMessage(customMember.getId(), messageRequest3);

                Count customCount = Count.of(customMember);
                countRepository.save(customCount);
                customCount.changeMaxCount(1000);


                for (int i = 10; i < members.size(); i++) {
                    visitService.postLike(members.get(i).getId(), customMember.getId(), true);
                }

                Member customMember2 = Member.builder()
                                            .userProviderId("2955591080")
                                            .username("선종우")
                                            .password("ddd")
                                            .registrationSource(RegistrationSource.KAKAO)
                                            .position(Position.BACK_END)
                                            .intro("안녕하세요")
                                            .imgUrl("https://i.pravatar.cc/150?u=fake@pravatar.com")
                                            .build();


                List<Interest> customIterests2 = Arrays.asList(Interest.of("BACK_END", customMember2),
                        Interest.of("FRONT_END", customMember2),
                        Interest.of("DESIGNER", customMember2),
                        Interest.of("PM_PO", customMember2)

                );

                List<Skill> customSkilll2 = skills.stream()
                                                 .filter(skill -> skill.getPosition() == customMember.getPosition())
                                                 .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                                                 .toList().subList(0, 4);

                List<SkillMember> skillMemberTable2 = customSkilll.stream().map(skill -> SkillMember.of(skill, customMember)).toList();
                memberRepository.save(customMember2);
                interestRepository.saveAll(customIterests2);
                skillMemberRepository.saveAll(skillMemberTable2);
                for (Member member : members) {
                    visitService.postLike(member.getId(), customMember2.getId(), true);
                }


            }


        });


    }

    private static List<Skill> skillSetup() {
        List<Skill> skillList = Arrays.asList(
                Skill.of("데이터 분석", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("요구사항 정의", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("문제 정의", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("프로젝트 관리", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("와이어프레임", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("논리적인 커뮤니케이션", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("다양한 부서와 협업 가능", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("문서 작성", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("피그마", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("\"SQL\"L", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("GA", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("PPT", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
                Skill.of("UX리서치", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("UI설계", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("GUI디자인", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("그래픽디자인", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("인터렉션디자인", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("모션디자인", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("BX디자인", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("프로토타이핑", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("UX Writing", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("디자인 시스템", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("피그마", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("스케치", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("포토샵", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("프로토파이", Position.valueOf("DESIGNER"), SkillCategory.FRAME_WORK),
                Skill.of("React", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Next", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Vue", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Angular", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Typescript", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Javascript", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("3D", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Html", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Css", Position.valueOf("FRONT_END"), SkillCategory.FRAME_WORK),
                Skill.of("Java", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Python", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("PHP", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Javascript", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Kotlin", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("C++", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("C#", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("ORM", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("RDB", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("No-SQL", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Cache", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Message Queue", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("CI/CD", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("Cloud Infra", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK),
                Skill.of("형상관리", Position.valueOf("BACK_END"), SkillCategory.FRAME_WORK)
        );

        return skillList;
    }
}
