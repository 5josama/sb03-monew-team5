package com.sprint5team.monew.domain.interest.mapper;

import com.sprint5team.monew.domain.interest.dto.InterestDto;
import com.sprint5team.monew.domain.interest.entity.Interest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.mapper
 * FileName     : InterestMapper
 * Author       : dounguk
 * Date         : 2025. 7. 13.
 */
@Mapper(componentModel = "spring")
public interface InterestMapper {

    @Mapping(target = "keywords", source = "keywords")
    @Mapping(target = "subscribedByMe", source = "subscribedByMe")
    InterestDto toDto(Interest interest, List<String> keywords, Boolean subscribedByMe);
}
