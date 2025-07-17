package com.sprint5team.monew.domain.interest.repository;

import com.sprint5team.monew.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.repository
 * FileName     : InterestRepostory
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
public interface InterestRepository extends JpaRepository<Interest, UUID>, InterestRepositoryCustom {
    // 정확히 비교
    boolean existsByNameEqualsIgnoreCase(String name);

    // 유사 비교
    @Query(value = """
    SELECT EXISTS (
        SELECT 1
        FROM tbl_interest
        WHERE similarity(lower(name), lower(:name)) > :threshold
    )
    """, nativeQuery = true)
    boolean existsSimilarName(@Param("name") String name, @Param("threshold") double threshold);
}
