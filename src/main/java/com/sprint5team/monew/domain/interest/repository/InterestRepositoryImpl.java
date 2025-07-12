package com.sprint5team.monew.domain.interest.repository;

import com.querydsl.core.BooleanBuilder;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.entity.QInterest;
import com.sprint5team.monew.domain.keyword.entity.QKeyword;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.repository
 * FileName     : InterestRepositoryImpl
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
public class InterestRepositoryImpl implements InterestRepositoryCustom{

    @Override
    public List<Interest> findAllInterestByRequest(CursorPageRequest request) {

        QInterest interest = QInterest.interest;
        QKeyword keyword = QKeyword.keyword;
        BooleanBuilder where = new BooleanBuilder();

        if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
            where.and(keyword.name.containsIgnoreCase(request.getKeyword()));
        }

        if(request.getCursor()!= null && request.getAfter() != null) {

        }


        return List.of();
    }
}
