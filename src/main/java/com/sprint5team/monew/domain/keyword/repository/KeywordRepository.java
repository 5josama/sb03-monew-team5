package com.sprint5team.monew.domain.keyword.repository;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.repository
 * FileName     : KeywordRepository
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
public interface KeywordRepository extends JpaRepository<Keyword, UUID> {
    List<Keyword> findAllByInterestIn(List<Interest> interests);

    List<Keyword> findAllByInterestId(UUID interestId);

    @Query("SELECT k FROM Keyword k JOIN FETCH k.interest")
    List<Keyword> findAllWithInterest();
}
