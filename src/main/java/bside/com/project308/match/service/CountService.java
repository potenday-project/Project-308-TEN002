package bside.com.project308.match.service;

import bside.com.project308.common.constant.ResponseCode;
import bside.com.project308.common.exception.InvalidAccessException;
import bside.com.project308.common.exception.ResourceNotFoundException;
import bside.com.project308.match.entity.Count;
import bside.com.project308.match.repository.CountRepository;
import bside.com.project308.member.entity.Member;
import bside.com.project308.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class CountService {

    private final CountRepository countRepository;
    private final MemberRepository memberRepository;

    public Integer getMatchCount(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Count memberCount = countRepository.findByMember(member).orElseGet(() -> {

            Count newCount = Count.of(member);
            countRepository.save(newCount);
            return newCount;
        });

        if(memberCount.isExhausted()){
            throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.MATCH_COUNT_EXHAUSTED);
        }

        return memberCount.getCurCount();

    }

    public Integer matchUserAndGetMatchCount(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new ResourceNotFoundException(ResponseCode.MEMBER_NOT_FOUND));
        Count memberCount = countRepository.findByMember(member).orElseGet(() -> {

            Count newCount = Count.of(member);
            countRepository.save(newCount);
            return newCount;
        });

        if(memberCount.isExhausted()){
            throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.MATCH_COUNT_EXHAUSTED);
        }

        memberCount.useMatch();
        return memberCount.getCurCount();

    }
}
