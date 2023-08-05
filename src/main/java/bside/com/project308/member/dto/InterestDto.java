package bside.com.project308.member.dto;

import bside.com.project308.member.entity.Interest;

public record InterestDto(
        Long id,
        String interest,
        MemberDto memberDto
) {

    public static InterestDto from(Interest interest) {
        return new InterestDto(interest.getId(), interest.getInterest(), MemberDto.from(interest.getMember()));
    }
}
