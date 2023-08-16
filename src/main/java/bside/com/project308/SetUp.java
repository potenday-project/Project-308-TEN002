package bside.com.project308;

import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.SwipeRepository;
import bside.com.project308.match.service.MatchService;
import bside.com.project308.match.service.SwipeService;
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
import bside.com.project308.message.service.MessageRoomService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "spring.config.activate", name = "on-profile", havingValue = "local")

public class SetUp {

    private final MemberRepository memberRepository;
    private final SkillMemberRepository skillMemberRepository;
    private final SkillRepository skillRepository;
    private final InterestRepository interestRepository;
    private final SwipeRepository swipeRepository;
    private final MatchRepository matchRepository;
    private final PlatformTransactionManager txManager;
    private final MatchService matchService;
    private final SwipeService swipeService;
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

                List<Member> members = new ArrayList<>();
                List<Interest> interests = new ArrayList<>();
                List<Skill> skills = skillSetup();
                skillRepository.saveAll(skills);


                List<SkillMember> skillMembers = new ArrayList<>();
                Position[] values = Position.values();

                Member initialMemberSun = Member.builder()
                                                .userProviderId("1")
                                                .username("tecky")
                                                .password("ddd")
                                                .registrationSource(RegistrationSource.KAKAO)
                                                .position(Position.BACK_END)
                                                .intro("안녕하세요! 저희는 tecky 팀입니다.")
                                                .imgUrl("https://project-308.kro.kr/images/logo.png")
                                                .build();


                List<Interest> initailIterest = Arrays.asList(Interest.of("BACK_END", initialMemberSun),
                        Interest.of("FRONT_END", initialMemberSun),
                        Interest.of("DESIGNER", initialMemberSun),
                        Interest.of("PM_PO", initialMemberSun)

                );

                List<Skill> initialSelectedSkill = skills.stream()
                                                  .filter(skill -> skill.getPosition() == initialMemberSun.getPosition())
                                                  .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                                                  .toList().subList(0, 4);

                List<SkillMember> initialSkillMemberTable = initialSelectedSkill.stream().map(skill -> SkillMember.of(skill, initialMemberSun)).toList();



                memberRepository.save(initialMemberSun);
                interestRepository.saveAll(initailIterest);
                skillMemberRepository.saveAll(initialSkillMemberTable);

 /*               Member initialMemberJosh = Member.builder()
                                                .userProviderId("2958207040")
                                                .username("JOSH")
                                                .password("ddd")
                                                .registrationSource(RegistrationSource.KAKAO)
                                                .position(Position.BACK_END)
                                                .intro("안녕하세요! Techky팀의 디자이너입니다.")
                                                .imgUrl("http://k.kakaocdn.net/dn/1Yl2a/btrs3Uj2P7b/KJ7KXQUNPma1FXdibktkQ0/img_640x640.jpg")
                                                .build();

                initialSelectedSkill = skills.stream()
                                                         .filter(skill -> skill.getPosition() == initialMemberJosh.getPosition())
                                                         .sorted((o1, o2) -> ThreadLocalRandom.current().nextInt(-1, 2))
                                                         .toList().subList(0, 4);

                initialSkillMemberTable = initialSelectedSkill.stream().map(skill -> SkillMember.of(skill, initialMemberJosh)).toList();
                skillMembers.addAll(initialSkillMemberTable);
                initailIterest = Arrays.asList(Interest.of("BACK_END", initialMemberJosh),
                        Interest.of("FRONT_END", initialMemberJosh),
                        Interest.of("DESIGNER", initialMemberJosh),
                        Interest.of("PM_PO", initialMemberJosh));

                memberRepository.save(initialMemberJosh);
                interestRepository.saveAll(initailIterest);
                skillMemberRepository.saveAll(skillMembers);*/
                long start = 2000000000L;
                long end = 3000000000L;
                long range = end - start + 1;
                Random generator = new Random();

                String intro1 = """
                        안녕하세요! 저는 백엔드 개발자 재경입니다. 
                        웹 및 앱 서비스를 만드는 것을 좋아하며, 안정적이고 확장 가능한 서버 측 시스템을 구축하는 것에 흥미를 느낍니다. 
                        다양한 프로그래밍 언어와 기술에 능숙하게 다룰 수 있으며, 데이터베이스 설계와 최적화에도 높은 관심을 가지고 있습니다. 
                        프로젝트를 완성하기 위해 끊임없이 학습하고, 최고의 결과물을 제공하기 위해 노력합니다. 
                        백엔드 개발을 통해 사용자들에게 편의와 가치를 제공하는데 기여하고 싶습니다. 
                        새로운 도전과 성장을 위해 항상 준비하고 있으며, 프로페셔널한 개발자로서 끊임없이 발전하고 싶습니다. 
                        함께 일하면 더 나은 미래를 만들 수 있다고 믿습니다!""";

                String intro2 = """
                        3년차 디자이너입니다. 
                        사용자 경험을 개선하는 것에 열정을 가지고 있습니다. 
                        다양한 디자인 분야에서 경험을 쌓았으며, 웹 디자인, 그래픽 디자인, UI/UX 디자인 등 다양한 영역에 능숙합니다. 
                        디자인 프로세스에 대한 깊은 이해와 문제 해결 능력을 가지고 있어, 프로젝트를 성공적으로 완료하는 데 최선을 다하고 있습니다.
                        팀에서 협업하며 아이디어를 공유하고 실현하는 것을 즐깁니다. 
                        
                        언제든 가볍게 좋아요 눌러주세요~ 
                        """;

                String intro3 = """
                        취업 준비생 Lucy라고 합니다 
                        사용자들이 편리하게 이용할 수 있는 인터페이스를 만들고 싶습니다. 
                        다양한 프론트엔드 기술과 도구를 활용하여 직관적이고 반응형 웹사이트를 개발하는 데 자신감을 가지고 있습니다.
                        HTML, CSS, JavaScript를 다루는 데 능숙하며, 최신 프론트엔드 프레임워크와 라이브러리에도 관심을 가지고 공부하고 있습니다. 
                        사용자 경험을 최적화하기 위해 UX/UI 디자인과 웹 접근성을 고려하며 작업하고, 효율적인 코드 작성과 모범 사례를 따르려고 노력해요                        
                        tecky를 통해 같이 사이드프로젝트를 진행할 백엔드 엔지니어를 찾고 있어요 
                        """;

                String intro4 = """
                        안녕하세요! 기획 2년차 유나입니다.
                        제품 개발과 관련된 경험을 통해 사용자의 니즈를 이해하고 그에 맞는 제품을 설계하고 개선해왔습니다. 
                        고객과 사용자의 요구사항을 수집하고 우선순위를 결정하는 과정에서는 뛰어난 커뮤니케이션과 문제 해결 능력이 필요합니다. 저는 이러한 능력을 가지고 있으며, 효율적인 제품 개발을 위해 팀과 함께 일하는 것을 즐깁니다.
                        프로젝트 매니징은 계획, 일정 관리, 리스크 관리 등 다양한 측면을 다루어야 합니다. 제가 수행한 프로젝트들은 예산 내에서 성공적으로 완료되었으며, 이를 위해 프로젝트 팀 간의 협업과 의사결정 능력을 활용했습니다.
                        끊임없이 변화하는 환경에서도 조직을 효과적으로 이끄는 능력을 가지고 있습니다. 변화에 빠르게 적응하고, 프로젝트의 목표를 이루기 위해 노력합니다. 사용자 중심의 제품을 개발하고, 팀의 역량을 최대한 발휘하며 팀원들이 동기부여를 느낄 수 있도록 지원합니다.
                        새로운 도전을 좋아하며 지식을 끊임없이 갱신하고 발전하는 것을 목표로 삼고 있습니다. 프로젝트와 제품 개발의 성공을 위해 열정적으로 노력하고, 팀원과 협업하여 더 큰 가치를 창출하는 것이 제 목표입니다.
                        """;
                List<String> intros = Arrays.asList(new String[]{intro1, intro2, intro3, intro4});
                List<String> names = Arrays.asList(new String[]{"재경", "Paul", "Lucy", "유나"});
                for (int i = 0; i < 4; i++) {
                    Member member = Member.builder()
                                          .userProviderId(String.valueOf(1000 + i))
                                          .username(names.get(i))
                                          .password("ddd")
                                          .registrationSource(RegistrationSource.KAKAO)
                                          .position(values[i])
                                          .intro(intros.get(i))
                                          .imgUrl("https://project-308.kro.kr/images/" + (i + 1)+ ".png")
                                          .build();
                    interests.addAll(Arrays.asList(new Interest[]{Interest.of(values[i].toString(), member), Interest.of(values[(i + 2) % 4].toString(), member)}));

                    members.add(member);
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

/*

                for (int i = 0; i < 10; i++) {
                    //long id = (long)(generator.nextDouble() * range + start);
                    Member member = Member.builder()
                                          .userProviderId(String.valueOf(1000 + i))
                                          .username("user" + i)
                                          .password("ddd")
                                          .registrationSource(RegistrationSource.KAKAO)
                                          .position(values[i % 4])
                                          .intro()
                                          .imgUrl("https://project-308.kro.kr/images/" + i + ".png")
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
*/


/*                //1번 멤버는 모든 사람과 매칭됨
                List<Match> matches = new ArrayList<>();
                for (int i = 1; i < members.size(); i++) {
                    matchService.createMatch(members.get(0), members.get(i));
                }

                List<Visit> visits1 = new ArrayList<>();

                //2번 멤버는 1번 외 모든 사용자에 대해 좋아요를 눌러놓음
                for (int i = 2; i < members.size(); i++) {
                    Visit visit = Visit.of(members.get(1), members.get(i), true);
                    visits1.add(visit);

                }*/

              //  visitRepository.saveAll(visits1);
/*                List<Visit> visits2 = new ArrayList<>();
                //3 ~ 4멤버는 자기보다 10번째 더 많은 사람까지 좋아요를 눌러놓음
                for (int i = 3; i < 5; i++) {
                    for (int j = i + 1; j < i + 10; j++) {
                        Visit visit = Visit.of(members.get(i), members.get(j), true);
                        visits2.add(visit);
                    }
                }

                visitRepository.saveAll(visits2);*/
/*

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
                IntStream.rangeClosed(1, 5)
                                 .forEach(i -> visitService.postLike(members.get(i).getId(), customMember.getId(), true));




                matchService.createMatch(customMember, members.get(3));
                matchService.createMatch(customMember, members.get(5));
                matchService.createMatch(customMember, members.get(12));


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
*/
/*


                for (int i = 10; i < members.size(); i++) {
                    visitService.postLike(members.get(i).getId(), customMember.getId(), true);
                }
*/

   /*             Member customMember2 = Member.builder()
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
*/

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
                Skill.of("\"SQL\"", Position.valueOf("PM_PO"), SkillCategory.FRAME_WORK),
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
