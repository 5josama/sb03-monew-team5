package com.sprint5team.monew.domain.keyword.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.dto
 * FileName     : InterestUpdateRequest
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */
public record InterestUpdateRequest(
    @NotNull @Size(min = 1, max = 10)
    List<@NotBlank @Size(max = 20) String> keywords
) {
}
