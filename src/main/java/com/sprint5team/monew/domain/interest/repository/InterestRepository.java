package com.sprint5team.monew.domain.interest.repository;

import com.sprint5team.monew.domain.interest.entity.Interest;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.repository
 * FileName     : InterestRepostory
 * Author       : dounguk
 * Date         : 2025. 7. 11.
 */
public interface InterestRepository extends JpaRepository<Interest, UUID>, InterestRepositoryCustom {

}
