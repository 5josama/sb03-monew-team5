package com.sprint5team.monew.domain.interest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.dto
 * FileName     : CursorPageRequest
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */

@AllArgsConstructor
@Builder
@Getter
@Setter
public class CursorPageRequest {
    private String keyword;

    @Pattern(regexp = "name|subscriberCount", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotBlank
    private String orderBy;

    @Pattern(regexp = "ASC|DESC", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotBlank
    private String direction;

    private String cursor;

    private Instant after;

    @NotBlank
    private Integer limit;

    @NotBlank
    private UUID userId;
}
