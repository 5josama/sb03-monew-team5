package com.sprint5team.monew.domain.user_interest.mapper;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import org.mapstruct.Named;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PackageName  : com.sprint5team.monew.domain.userinterest.mapper
 * FileName     : UserInterestMapper
 * Author       : dounguk
 * Date         : 2025. 7. 18.
 */
@Mapper(componentModel = "spring", imports = Instant.class)
public interface UserInterestMapperTemp {

    @Mapping(target = "interestSubscriberCount", source = "interest.subscriberCount")
    @Mapping(target = "interestKeywords", source = "interest.keywords", qualifiedByName = "keywordListToNameList")
    @Mapping(target = "interestName", source = "interest.name")
    @Mapping(target = "interestId",source = "interest.id")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", expression = "java(Instant.now())")
    SubscriptionDto toDto(UUID id, Interest interest);

    @Named("keywordListToNameList")
    default List<String> mapKeywordListToNameList(List<Keyword> keywords) {
        return keywords.stream()
            .map(Keyword::getName)
            .collect(Collectors.toList());
    }
}
