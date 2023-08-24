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

    public Count getSwipeCount(Member member) {
        Count swipeCount = countRepository.findByMember(member).orElseGet(() -> {
            Count newCount = Count.of(member);
            countRepository.save(newCount);
            return newCount;
        });


        return swipeCount;

    }

    public Count useSwipeAndGetCount(Member member) {

        Count swipeCount = countRepository.findByMember(member).orElseGet(() -> {
            Count newCount = Count.of(member);
            countRepository.save(newCount);
            return newCount;
        });

        if(swipeCount.isExhausted()){
            throw new InvalidAccessException(HttpStatus.BAD_REQUEST, ResponseCode.MATCH_COUNT_EXHAUSTED);
        }

        swipeCount.useMatch();
        return swipeCount;

    }
}
