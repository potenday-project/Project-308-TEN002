package bside.com.project308.notification.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.match.dto.MatchDto;
import bside.com.project308.match.dto.response.MatchResponse;
import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import bside.com.project308.member.dto.MemberDto;
import bside.com.project308.notification.constant.NotificationType;
import bside.com.project308.notification.dto.MatchNotificationResponse;
import bside.com.project308.notification.dto.MessageNotificationResponse;
import bside.com.project308.notification.dto.NotificationResponse;
import bside.com.project308.notification.repository.EmitterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;

@RequiredArgsConstructor
@Service
@Slf4j
public class SseTest {


    private final EmitterRepository emitterRepository;
    private final ObjectMapper objectMapper;
    @Async
    public void sseTestSend(Long memberId, SseEmitter sseEmitter) {
        /*MemberDto fromMember = new MemberDto(1L, "dddd", "name1", "ddd", Position.BACK_END, RegistrationSource.KAKAO, "dff", "https://project-308.kro.kr/images/1.png", null, null);
        MemberDto toMember = new MemberDto(1L, "dddd", "name1", "ddd", Position.BACK_END, RegistrationSource.KAKAO, "dff", "https://project-308.kro.kr/images/1.png", null, null);
        MatchDto matchDto = new MatchDto(1L, fromMember, toMember, LocalDateTime.now(), false);

        MatchNotificationResponse matchResponse = new MatchNotificationResponse(MatchResponse.from(matchDto), 1L);
        NotificationResponse notificationResponse = new NotificationResponse<MatchNotificationResponse>(null, NotificationType.MATCH, matchResponse);*/
        MessageNotificationResponse messageNotificationResponse = new MessageNotificationResponse(22L,
                11L,
                10L,
                "sdfsdf");
        log.debug("new message event");
        NotificationResponse notificationResponse = new NotificationResponse<MessageNotificationResponse>(null, NotificationType.MESSAGE, messageNotificationResponse);



        for(int i = 0 ; i < 100; i++){
            try {
                sleep(10 * 1000);

                send(memberId, notificationResponse);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                break;
            }
        }

    }
    public void send(Long memberId, NotificationResponse notificationResponse) {
        List<SseEmitter> sseEmitters = emitterRepository.get(memberId);
        if (CollectionUtils.isEmpty(sseEmitters)) {
            throw new RuntimeException();
        }
        sseEmitters.stream().forEach(sseEmitter -> {
                    String eventId = makeEventId(memberId);
                    NotificationResponse responseWithId = new NotificationResponse(eventId, notificationResponse.type(), notificationResponse.content());
                    sendNotification(sseEmitter, eventId, responseWithId);
                }
        );
    }

    private void sendNotification(SseEmitter emitter, String eventId, Object data) {
        Response response = Response.success(ResponseCode.SUCCESS.getCode(), data);
        try {
            log.info("sse data send");
            emitter.send(SseEmitter
                    .event()
                    .id(eventId)
                    .data(objectMapper.writeValueAsString(response))
            );
        } catch (IOException e) {
            log.error("message send error");
        }
    }


    private String makeEventId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }


}
