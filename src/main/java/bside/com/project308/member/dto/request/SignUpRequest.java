package bside.com.project308.member.dto.request;

import bside.com.project308.member.constant.Position;
import bside.com.project308.member.constant.RegistrationSource;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SignUpRequest(@NotBlank
                            String userProviderId,
                            @NotBlank
                            String username,
                            @NotNull
                            Position position,
                            @NotNull
                            RegistrationSource registrationSource,
                            @NotBlank
                            String imgUrl,
                            @NotNull
                            List<String> skill,
                            @NotBlank
                            String intro,
                            @NotNull
                            List<String> interest) {
}
