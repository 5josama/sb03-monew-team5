package com.sprint5team.monew.domain.user_interest.repository;

import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * PackageName  : com.sprint5team.monew.domain.user_interest.repository
 * FileName     : UserInterestRepository
 * Author       : dounguk
 * Date         : 2025. 7. 12.
 */
public interface UserInterestRepository extends JpaRepository<UserInterest, UUID> {

    Set<UserInterest> findByUserId(UUID userId);

    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

    Optional<UserInterest> findByUserIdAndInterestId(UUID userId, UUID interestId);
}
