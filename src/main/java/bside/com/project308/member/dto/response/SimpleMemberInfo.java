package bside.com.project308.member.dto.response;

import bside.com.project308.member.constant.Position;

public record SimpleMemberInfo(Long id,
                                String username,
                                Position position,
                                String imgUrl)
{
}
