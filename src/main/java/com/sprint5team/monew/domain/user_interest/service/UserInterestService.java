package com.sprint5team.monew.domain.user_interest.service;

import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.userinterest.service
 * FileName     : UserInterestService
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
public interface UserInterestService {

    SubscriptionDto registerSubscription(UUID interestId, UUID userId);

    void unfollowInterest(UUID interestId, UUID userId);
}
