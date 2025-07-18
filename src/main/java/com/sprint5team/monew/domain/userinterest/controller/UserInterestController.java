package com.sprint5team.monew.domain.userinterest.controller;

import com.sprint5team.monew.domain.userinterest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.userinterest.repository.UserInterestRepository;
import com.sprint5team.monew.domain.userinterest.service.UserInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.userinterest.controller
 * FileName     : UserInterestController
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/interests")
public class UserInterestController {

    private final UserInterestService userInterestService;

    @PostMapping("/{interestId}/subscriptions")
    public ResponseEntity<SubscriptionDto> registerSubscription(
        @PathVariable UUID interestId,
        @RequestHeader(name = "Monew-Request-User-ID") UUID userId
    ) {
        return ResponseEntity.ok().body(userInterestService.registerSubscription(interestId, userId));
    }
}
