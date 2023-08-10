package bside.com.project308.common.util;

import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.match.repository.MatchRepository;
import bside.com.project308.match.repository.TodayMatchRepository;
import bside.com.project308.match.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@EnableScheduling
@RequiredArgsConstructor
@Component
@Slf4j
public class CronService {

    private final CountRepository countRepository;
    private final TodayMatchRepository todayMatchRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void resetVisitCount() {
        log.info("count is reset {}", LocalDateTime.now());
        countRepository.resetCount();
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void resetTodayMatch() {
        log.info("count is reset {}", LocalDateTime.now());
        todayMatchRepository.resetTodayMatch();
    }
}
