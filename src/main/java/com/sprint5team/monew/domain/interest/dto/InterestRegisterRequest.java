package com.sprint5team.monew.domain.interest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.dto
 * FileName     : InterestRegisterRequest
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */
@Builder
public record InterestRegisterRequest(
    @NotNull @Size(min = 1, max = 50)
    String name,

    @NotNull @Size(min = 1, max = 10)
    List<@NotBlank @Size(max = 20) String> keywords
) { }
