package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class InterestMatcher {

    private final KeywordRepository keywordRepository;

    public List<Interest> match(Article article) {
        String content = article.getTitle() + " " + article.getSummary();
        log.debug("기사 제목+요약: {}", content);

        List<Keyword> allKeywords = keywordRepository.findAllWithInterest();
        log.debug("전체 키워드 개수: {}", allKeywords.size());

        Set<Interest> matched = allKeywords.stream()
                .filter(k -> content.contains(k.getName()))
                .map(Keyword::getInterest)
                .collect(Collectors.toSet());

        log.debug("최종 매칭 관심사 수: {}", matched.size());
        return new ArrayList<>(matched);
    }
}