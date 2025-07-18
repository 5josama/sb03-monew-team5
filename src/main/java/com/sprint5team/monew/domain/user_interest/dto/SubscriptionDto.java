package com.sprint5team.monew.domain.user_interest.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.dto
 * FileName     : SubscriptionDto
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */
@Builder
public record SubscriptionDto(
    UUID id,
    UUID interestId,
    String interestName,
    List<String> interestKeywords,
    Long interestSubscriberCount,
    Instant createdAt
) {
}
