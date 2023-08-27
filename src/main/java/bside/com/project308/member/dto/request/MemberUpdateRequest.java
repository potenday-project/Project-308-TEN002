package bside.com.project308.member.dto.request;

import bside.com.project308.member.constant.Position;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

public record MemberUpdateRequest(String username,
                                  String intro,
                                  List<String> skill) {

}
