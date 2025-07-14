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
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService{
    private static final String NAME = "name";

    private final InterestRepository interestRepository;

    private final KeywordRepository keywordRepository;

    private final UserInterestRepository userInterestRepository;

    private final InterestMapper interestMapper;

    public CursorPageResponseInterestDto generateCursorPage(CursorPageRequest request) {

        log.info("1. content 조회");
        List<Interest> contents = interestRepository.findAllInterestByRequest(request);


        Set<UUID> userInterestIds = userInterestRepository.findByUserId(request.getUserId()).stream()
            .map(userInterest -> userInterest.getInterest().getId())
            .collect(Collectors.toSet());

        log.info("2. 커서 생성");
        long totalElements = interestRepository.countTotalElements(request);
        String nextCursor = null;
        Instant nextAfter = null;
        boolean hasNext = contents.size() == request.getLimit() + 1;

        if (hasNext) {
            log.info("다음 페이지 있음");
            contents = contents.subList(0, request.getLimit());
            Interest lastContent = contents.get(contents.size() - 1);
            nextAfter = lastContent.getCreatedAt();

            switch (request.getOrderBy()) {
                case NAME -> nextCursor = lastContent.getName();
                default -> nextCursor = String.valueOf(lastContent.getSubscriberCount());
            }
        }

        log.info("3. uuid와 keywords로 매핑");
        Map<UUID, List<String>> interestKeywordMap = keywordRepository.findAllByInterestIn(contents).stream()
            .collect(Collectors.groupingBy(
                keyword -> keyword.getInterest().getId(),
                Collectors.mapping(Keyword::getName, Collectors.toList())
            ));

        log.info("4. InterestDto로 매핑");
        List<InterestDto> interestDtos = contents.stream()
            .map(interest -> {
                List<String> keywordNames = interestKeywordMap.getOrDefault(interest.getId(), List.of());
                boolean subscribedByMe = userInterestIds.contains(interest.getId());
                return interestMapper.toDto(interest, keywordNames, subscribedByMe);
            }).toList();

        log.info("5. CursorPageResponseInterestDto로 매핑");
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
