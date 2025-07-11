package com.sprint5team.monew.domain.interest.service;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.service
 * FileName     : InterestService
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
@Validated
@Service
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private final InterestRepository interestRepository;

    public CursorPageResponseInterestDto generateCursorPage(@Valid CursorPageRequest request) {

        // contents
        List<Interest> contents = interestRepository.findAllInterestByRequest(request);
        // interest 싹 다 가져옴
        // keyword repository 통해서 싹 다 가져옴(name only)
        // user_interest 통해서 subscribedByMe 확인



        return null;
    }
}
