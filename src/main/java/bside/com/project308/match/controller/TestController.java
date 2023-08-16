package bside.com.project308.match.controller;

import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.SwipeRepository;
import bside.com.project308.match.repository.VisitedMemberCursorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")

public class TestController {
    private final CountRepository countRepository;
    private final MatchRepository matchRepository;
    private final SwipeRepository swipeRepository;

    @GetMapping("/reset")
    public String reset() {
        countRepository.deleteAll();
        matchRepository.deleteAll();
        swipeRepository.deleteAll();
        return "초기화 완료";
    }
}
