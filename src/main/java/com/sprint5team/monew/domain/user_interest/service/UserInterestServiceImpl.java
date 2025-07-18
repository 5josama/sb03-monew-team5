package com.sprint5team.monew.domain.user_interest.service;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.exception.InterestNotExistsException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.exception.UserInterestAlreadyExistsException;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * PackageName  : com.sprint5team.monew.domain.userinterest.service
 * FileName     : UserInterestServiceImpl
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestRepository userInterestRepository;

    private final UserRepository userRepository;

    private final InterestRepository interestRepository;

    private final UserInterestMapper userInterestMapper;

    @Override
    public SubscriptionDto registerSubscription(UUID interestId, UUID userId) {

        log.info("1. 사용자 유효성 확인");
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        log.info("2. 관심사 유효성 확인");
        Interest interest = interestRepository.findById(interestId).orElseThrow(InterestNotExistsException::new);

        log.info("3. 구독 중복 여부 확인");
        if (userInterestRepository.existsByUserIdAndInterestId(userId, interestId)) throw new UserInterestAlreadyExistsException();

        log.info("4. 구독 정보 저장");
        UserInterest userInterest = UserInterest.builder()
            .user(user)
            .interest(interest)
            .build();
        userInterestRepository.save(userInterest);

        log.info("5. 관심사 구독자 수 증가");
        interest.subscribe();

        return userInterestMapper.toDto(userInterest);
    }

    // TODO 구독 취소
    @Override
    public void unsubscribeInterest(UUID interestId, UUID userId) {
        /**
         * 1. 구독 여부 확인 - exception
         * 2. 요청자 확인 - exception
         * 3. 관심사 확인 - exception
         * 4. 구독 삭제
         * 5. 관심사 구독 수 -1
         * 5-1  관심사가 0 또는 작을 경우 - exception(SubscriberNotMatchesException 추가)
         * 5-2  문제 없으면 -1
         */

        UserInterest userInterest = userInterestRepository.findByUserIdAndInterestId(userId, interestId).orElseThrow(UserInterestAlreadyExistsException::new);
        if(!userRepository.existsById(userId)) throw new UserNotFoundException();
        Interest interest = interestRepository.findById(interestId).orElseThrow(InterestNotExistsException::new);
        interest.unsubscribe();
        userInterestRepository.delete(userInterest);
    }
}
