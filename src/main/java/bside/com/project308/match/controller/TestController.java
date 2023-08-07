package bside.com.project308.match.controller;

import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@ConditionalOnProperty(value = "spring.config.activate.on-profile", havingValue = "local")
public class TestController {
    private final CountRepository countRepository;
    private final MatchRepository matchRepository;
    private final VisitedMemberCursorRepository visitedMemberCursorRepository;
    private final VisitRepository visitRepository;

    @GetMapping("/reset")
    public String reset(@PathVariable Long memberId) {
        countRepository.deleteAll();
        matchRepository.deleteAll();
        visitRepository.deleteAll();
        visitedMemberCursorRepository.deleteAll();
        return "초기화 완료";
    }
}
