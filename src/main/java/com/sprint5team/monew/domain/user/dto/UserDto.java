package com.sprint5team.monew.domain.user.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDto(

    UUID userId,
    String email,
    String nickname,
    Instant createdAt
) {
}
