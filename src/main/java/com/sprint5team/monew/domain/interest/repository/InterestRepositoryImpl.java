package com.sprint5team.monew.domain.interest.repository;

import com.querydsl.core.BooleanBuilder;
import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.entity.QInterest;

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

        QInterest qInterest = QInterest.interest;
        BooleanBuilder builder = new BooleanBuilder();



        return List.of();
    }
}
