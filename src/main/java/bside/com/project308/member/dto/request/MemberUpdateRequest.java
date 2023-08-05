package bside.com.project308.member.dto.request;

import bside.com.project308.member.constant.Position;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MemberUpdateRequest {
    private Long id;
    private String username;
    private String password;
    private Position position;
}
