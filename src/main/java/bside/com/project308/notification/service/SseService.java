package bside.com.project308.notification.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.response.Response;
import bside.com.project308.notification.dto.NotificationResponse;
import bside.com.project308.notification.repository.EmitterRepository;
import bside.com.project308.notification.repository.NotificationCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SseService {

    private static final Long DEFAULT_TIMEOUT = 10L * 1000 * 60;

    private final EmitterRepository emitterRepository;
    private final NotificationCache notificationCache;
    private final ObjectMapper objectMapper;
    private final SseTest sseTest;

    public SseEmitter subscribe(Long memberId, Long randomId, String lastEventId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, randomId, emitter);

        emitter.onTimeout(() -> {
            log.debug("sse timeout and delete");
            emitterRepository.delete(memberId, emitter);});
        emitter.onCompletion(() -> {
            log.debug("sse complete and delete");
            emitterRepository.delete(memberId, emitter);});

        String eventId = makeEventId(memberId);
        sendNotification(emitter, eventId, "EventStream Created");

/*        if(memberId == 25){
            log.debug("test created");
            sseTest.sseTestSend(memberId, emitter);
        }*/
        return emitter;
    }


    public void send(Long memberId, NotificationResponse notificationResponse) {

        List<SseEmitter> sseEmitters = emitterRepository.get(memberId);
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
            emitter.send(SseEmitter
                    .event()
                    .id(eventId)
                    .data(response)
                    

            );
            log.info("sse send log : {}", response.getData());
        } catch (IOException e) {
            log.error("message send error");
        }
    }


    private String makeEventId(Long memberId) {
        return memberId + "_" + System.currentTimeMillis();
    }

    private void sendLostData(String lastEventId, Long memberId, SseEmitter emitter) {
        Map<String, NotificationResponse> eventCache = notificationCache.getEventCache(String.valueOf(memberId));
        eventCache.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), entry.getValue()));
    }


}
