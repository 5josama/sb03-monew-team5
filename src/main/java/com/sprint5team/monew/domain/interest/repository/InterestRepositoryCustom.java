package com.sprint5team.monew.domain.interest.repository;

import com.sprint5team.monew.domain.interest.dto.CursorPageRequest;
import com.sprint5team.monew.domain.interest.entity.Interest;

import java.util.List;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.repository
 * FileName     : InterestRepositoryCustom
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
public interface InterestRepositoryCustom {

    long countTotalElements(CursorPageRequest request);

    List<Interest> findAllInterestByRequest(CursorPageRequest request);
}

