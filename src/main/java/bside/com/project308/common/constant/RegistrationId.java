package bside.com.project308.common.constant;

import lombok.Getter;

@Getter
public enum RegistrationId {
    KAKAO("kakao");
    private String registrationId;
    RegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
}
