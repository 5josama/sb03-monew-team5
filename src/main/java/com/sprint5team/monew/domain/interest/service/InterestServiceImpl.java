package com.sprint5team.monew.domain.interest.service;

import com.sprint5team.monew.domain.interest.exception.InterestNotExistsException;
import com.sprint5team.monew.domain.interest.exception.SimilarInterestException;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.dto.CursorPageResponseInterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.dto.InterestRegisterRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.keyword.dto.InterestUpdateRequest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.exception.NoKeywordsToUpdateException;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.interest.mapper.InterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
@RequiredArgsConstructor
@Transactional
public class InterestServiceImpl implements InterestService{
    private static final String NAME = "name";
    private static final double THRESHOLD = 0.6;

    private final InterestRepository interestRepository;

    private final KeywordRepository keywordRepository;

    private final UserInterestRepository userInterestRepository;

    private final InterestMapper interestMapper;

    @Transactional(readOnly = true)
    @Override
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

    @Override
    public InterestDto registerInterest(InterestRegisterRequest request) {
        String name = request.name().trim();

        log.info("분기 시작 test 환경에선 h2 로직 실행 prod 환경에서 postgresql 사용 로직 실행");
        log.info("1. 동일한 관심사 이름 있는지 확인");
        if(name.length()<=3){
            if(interestRepository.existsByNameEqualsIgnoreCase(name)) throw new SimilarInterestException();
        }else {
            if(interestRepository.existsSimilarName(name, THRESHOLD)) throw new SimilarInterestException();
        }

        log.info("2.유사 관심사 없음 생성 로직 실행");
        log.info("2-1. 관심사 저장");
        Interest interest = new Interest(name);
        interestRepository.save(interest);

        log.info("2-2.키워드 저장");
        List<Keyword> keywords= new ArrayList<>();
        for (String keywordName : request.keywords()) {
            Keyword keyword = new Keyword(keywordName, interest);
            keywords.add(keyword);
        }
        keywordRepository.saveAll(keywords);

        log.info("2-3. dto 만들어서 반환");
        InterestDto response = interestMapper.toDto(interest, request.keywords(), false);

        return response;
    }

    @Override
    public void deleteInterest(UUID interestId) {
        log.info("관심사 삭제");
        if(!interestRepository.existsById(interestId)) throw new InterestNotExistsException();

        interestRepository.deleteById(interestId);
    }

    @Override
    public InterestDto updateInterest(UUID interestId, InterestUpdateRequest request) {
        log.info("1. 관심사 탐색");
        Interest interest = interestRepository.findById(interestId)
            .orElseThrow(InterestNotExistsException::new);

        log.info("2. 추가할 키워드 있는지 조회 및 비교");
        List<String> newKeywordNames = request.keywords();
        List<Keyword> oldKeyword = keywordRepository.findAllByInterestId(interestId);

        Set<String> oldKeywordNames = new HashSet<>(oldKeyword.stream()
            .map(Keyword::getName)
            .collect(Collectors.toSet()));

        Set<String> newKeywordNameSet = new HashSet<>(newKeywordNames);

        log.info("3. 삭제하려는 키워드 있는지 확인");
        if (!newKeywordNames.containsAll(oldKeywordNames)) {
            log.info("3-1 삭제는 할 수 없음 exception");
            throw new NoKeywordsToUpdateException();
        }

        if (newKeywordNameSet.equals(oldKeywordNames)) {
            log.info("변경 없음, 그대로 응답");
            return interestMapper.toDto(interest, newKeywordNames, null);
        }

        log.info("4. 키워드 추가");
        List<Keyword> keywordsToSave = newKeywordNames.stream()
            .filter(name -> !oldKeywordNames.contains(name))
            .map(name -> new Keyword(name, interest))
            .collect(Collectors.toList());


        if (!keywordsToSave.isEmpty()) {
            keywordRepository.saveAll(keywordsToSave);
        }

        return interestMapper.toDto(interest, request.keywords(), null);
    }
}
