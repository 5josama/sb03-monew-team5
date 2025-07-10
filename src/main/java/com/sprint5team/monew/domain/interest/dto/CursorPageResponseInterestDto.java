package com.sprint5team.monew.domain.interest.dto;

import com.sprint5team.monew.domain.interest.entity.Interest;

import java.time.Instant;
import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.dto
 * FileName     : CursorPageResponseInterestDto
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */

public record CursorPageResponseInterestDto(
    List<InterestDto> content,
    String nextCursor,
    Instant nextAfter,
    Integer size,
    Long totalElements,
    boolean hasNext
) {
}
