package bside.com.project308.member.dto.request;

import bside.com.project308.member.constant.Position;

import java.util.List;

public record SignUpRequest(Position position,
                            List<String> skill,
                            String intro,
                            List<String> interest) {
}
