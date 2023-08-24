package bside.com.project308.notification.repository;

import bside.com.project308.notification.dto.NotificationResponse;
import org.springframework.stereotype.Component;

import javax.management.Notification;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class NotificationCache {
    private final Map<String, NotificationResponse> eventCache = new ConcurrentHashMap<>();

    public void saveEventCache(String eventCacheId, NotificationResponse event) {
        eventCache.put(eventCacheId, event);
    }

    public Map<String, NotificationResponse> getEventCache(String memberId) {
        return eventCache.entrySet().stream()
                         .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
