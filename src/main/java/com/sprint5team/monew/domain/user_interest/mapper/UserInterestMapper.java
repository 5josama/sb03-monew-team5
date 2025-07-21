package com.sprint5team.monew.domain.user_interest.mapper;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.keyword.repository.KeywordRepository;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserInterestMapper {

  private final KeywordRepository keywordRepository;

  public SubscriptionDto toDto(UserInterest userInterest) {
    Interest interest = userInterest.getInterest();
    List<String> keywords =
        keywordRepository
            .findAllByInterestIn(List.of(interest))
            .stream()
            .map(Keyword::getName)
            .toList();

    return new SubscriptionDto(
        userInterest.getId(),
        interest.getId(),
        interest.getName(),
        keywords,
        interest.getSubscriberCount(),
        userInterest.getCreatedAt()
    );
  }
}