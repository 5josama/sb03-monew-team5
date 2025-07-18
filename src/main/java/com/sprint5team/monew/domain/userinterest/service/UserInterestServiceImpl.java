package com.sprint5team.monew.domain.userinterest.service;

import com.sprint5team.monew.domain.interest.exception.InterestNotExistException;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.userinterest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.userinterest.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.userinterest.service
 * FileName     : UserInterestServiceImpl
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserInterestServiceImpl implements UserInterestService {

    private final UserInterestRepository userInterestRepository;

    private final UserRepository userRepository;

    private final InterestRepository interestRepository;

    @Override
    public SubscriptionDto registerSubscription(UUID interestId, UUID userId) {
        interestRepository.findById(interestId).orElseThrow(() -> new InterestNotExistException());

        return null;
    }
}
