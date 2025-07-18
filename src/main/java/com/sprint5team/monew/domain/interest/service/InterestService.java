package com.sprint5team.monew.domain.interest.service;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.keyword.dto.InterestUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.service
 * FileName     : InterestService
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@Validated
public interface InterestService{

    CursorPageResponseInterestDto generateCursorPage(@Valid CursorPageRequest request);

    InterestDto registerInterest(InterestRegisterRequest  request);

    void deleteInterest(UUID interestId);

    InterestDto updateInterest(UUID interestId, InterestUpdateRequest request, UUID userId);
}
