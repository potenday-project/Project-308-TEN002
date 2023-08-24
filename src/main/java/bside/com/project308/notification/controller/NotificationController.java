package bside.com.project308.notification.controller;

import bside.com.project308.notification.service.SseService;
import bside.com.project308.security.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final SseService sseService;

    @GetMapping(value = "/subscribe/{randomId}", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal UserPrincipal userPrincipal, @PathVariable Long randomId) {
        return sseService.subscribe(userPrincipal.id(), randomId, null);
    }

    @GetMapping(value = "/subscribe/test/{memberId}", produces = "text/event-stream")
    public SseEmitter subscribeTest(@PathVariable Long memberId) {
        return sseService.subscribe(memberId, 2345L, null);
    }
}
