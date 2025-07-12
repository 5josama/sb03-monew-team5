package com.sprint5team.monew.domain.interest.dto;

import lombok.Builder;

import java.util.List;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.dto
 * FileName     : InterestDto
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */
@Builder
public record InterestDto (
    UUID id,
    String name,
    List<String> keywords,
    long subscriberCount,
    boolean subscribedByMe
){
}
