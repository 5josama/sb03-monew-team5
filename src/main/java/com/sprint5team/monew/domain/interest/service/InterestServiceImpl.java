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

import java.time.Instant;
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
    private static final String NAME = "name";S

    private final InterestRepository interestRepository;

    private final KeywordRepository keywordRepository;

    private final UserInterestRepository userInterestRepository;

    private final InterestMapper interestMapper;


    public CursorPageResponseInterestDto generateCursorPage(@Valid CursorPageRequest request) {

        // 1. get contents
        List<Interest> contents = interestRepository.findAllInterestByRequest(request);

        // TODO 그냥 이름만 가져올까?
        Set<UUID> userInterestIds = userInterestRepository.findByUserId(request.getUserId()).stream()
            .map(userInterest -> userInterest.getInterest().getId())
            .collect(Collectors.toSet());

        // 2. make cursor
        long totalElements = interestRepository.countTotalElements(request);
        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = contents.size() == request.getLimit() + 1;

        if (hasNext) {
            contents = contents.subList(0,request.getLimit());
            Interest lastContent = contents.get(contents.size() - 1);
            nextAfter = lastContent.getCreatedAt();

            switch (request.getOrderBy()){
                case NAME ->  nextCursor = lastContent.getName();
                default ->  nextCursor = String.valueOf(lastContent.getSubscriberCount());
            }
        }

        // 3. map contents to interestDtos
        List<InterestDto> interestDtos = contents.stream()
            .map(interest -> {
                List<String> keywordNames = keywordRepository.findAllByInterest(interest).stream()
                    .map(Keyword::getName)
                    .toList();

                boolean subscribedByMe = userInterestIds.contains(interest.getId());

                return interestMapper.toDto(interest, keywordNames, subscribedByMe);
            }).toList();

        // 4. combine all together to CursorPageResponseInterestDto
        CursorPageResponseInterestDto result = CursorPageResponseInterestDto.builder()
            .content(interestDtos)
            .nextCursor(nextCursor)
            .nextAfter(nextAfter)
            .size(request.getLimit())
            .totalElements(totalElements)
            .hasNext(hasNext)
            .build();

        return result;
    }
}
