package com.sprint5team.monew.domain.interest.service;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepositoryImpl;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.InterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

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

    private final KeywordRepository keywordRepository;

    private final UserInterestRepository userInterestRepository;

    private final InterestMapper interestMapper;
    private final UserRepository userRepository;

    public CursorPageResponseInterestDto generateCursorPage(@Valid CursorPageRequest request) {

        // 1. contents
        List<Interest> contents = interestRepository.findAllInterestByRequest(request);

        // TODO 그냥 이름만 가져올까?
        Set<UUID> userInterestIds = userInterestRepository.findByUserId(request.getUserId()).stream()
            .map(userInterest -> userInterest.getInterest().getId())
            .collect(Collectors.toSet());

        List<InterestDto> interestDtos = contents.stream()
            .map(interest -> {
                List<String> keywordNames = keywordRepository.findAllByInterest(interest).stream()
                    .map(Keyword::getName)
                    .toList();

                boolean subscribedByMe = userInterestIds.contains(interest.getId());

                return interestMapper.toDto(interest, keywordNames, subscribedByMe);
            }).toList();

        // 2. cursor
        long totalElements = interestRepository.countTotalElements(request);




        return null;
    }
}
