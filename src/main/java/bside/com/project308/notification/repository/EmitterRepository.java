package bside.com.project308.notification.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.swing.text.html.Option;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class EmitterRepository {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long memberId, Long randomId, SseEmitter sseEmitter) {
        final String key = getUniqueKey(getKey(memberId), randomId);
        emitters.put(key, sseEmitter);
        return sseEmitter;
    }

    public List<SseEmitter> get(Long memberId) {
        final String memberKey = getKey(memberId);
        List<SseEmitter> memberEmitters = new ArrayList<>();

        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            if (entry.getKey().startsWith(memberKey)) {
                memberEmitters.add(entry.getValue());
            }
        }
        return memberEmitters;
    }

    public void delete(Long memberId, SseEmitter emitter) {
        final String memberKey = getKey(memberId);
        List<SseEmitter> memberEmitters = new ArrayList<>();
        for (Map.Entry<String, SseEmitter> entry : emitters.entrySet()) {
            if (entry.getKey().startsWith(memberKey) && entry.getValue() == emitter) {
                log.info("remove!!");
                log.info("{}", entry.getKey());
                emitters.remove(entry.getKey());
                break;
            }
        }
    }

    public Map<String, SseEmitter> findAllEmitterStartWithMemberId(String memberId) {
        return emitters.entrySet().stream()
                       .filter(entry -> entry.getKey().startsWith(memberId))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private String getKey(Long memberId) {
        return "notificationEmitter:ID:" + memberId + "_";
    }

    private String getUniqueKey(String memberKey, Long randomId) {
        return memberKey + randomId;
    }


}
