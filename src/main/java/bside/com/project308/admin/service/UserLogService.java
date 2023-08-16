package bside.com.project308.admin.service;

import bside.com.project308.admin.Type;
import bside.com.project308.admin.entity.UserLog;
import bside.com.project308.admin.repository.UserLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserLogService {

    private final UserLogRepository userLogRepository;

    public void saveUserLog(Long memberId, Type type) {
        UserLog userLog = new UserLog(memberId, type);
        userLogRepository.save(userLog);

    }
}
