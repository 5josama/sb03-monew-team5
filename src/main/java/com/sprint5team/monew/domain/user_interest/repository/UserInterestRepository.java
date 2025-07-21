package com.sprint5team.monew.domain.user_interest.repository;

import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.repository
 * FileName     : UserInterestRepository
 * Author       : dounguk
 * Date         : 2025. 7. 12.
 */
public interface UserInterestRepository extends JpaRepository<UserInterest, UUID> {

    Set<UserInterest> findByUserId(UUID userId);

    List<UserInterest> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);
}
