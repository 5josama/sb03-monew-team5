package com.sprint5team.monew.domain.user_interest.repository;

import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // 사용자 활동 내역 조회 시 사용
    List<UserInterest> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

    @Query("SELECT ui.user FROM UserInterest ui WHERE ui.interest.id = :interestId")
    List<User> findUsersByInterestId(@Param("interestId") UUID interestId);

    // 사용자 물리삭제 시 사용
    void deleteAllByUserId(UUID userId);
}