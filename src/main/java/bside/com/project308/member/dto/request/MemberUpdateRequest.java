package bside.com.project308.member.dto.request;

import bside.com.project308.member.constant.Position;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

//todo : argument resolver 고려
public record MemberUpdateRequest(String username,
                                  String position,
                                  String intro,
                                  String imgUrl,
                                  List<String> interest,
                                  List<String> skill) {

}
