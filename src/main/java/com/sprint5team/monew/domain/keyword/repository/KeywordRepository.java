package com.sprint5team.monew.domain.keyword.repository;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

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
    Set<Keyword> findAllByInterest(Interest interest);
}
