package com.sprint5team.monew.domain.user_interest.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

/**
 * PackageName  : com.sprint5team.domain.interest.user_interest.entity
 * FileName     : UserInterest
 * Author       : dounguk
 * Date         : 2025. 7. 10.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(name = "tbl_user_interest")
public class UserInterest extends BaseEntity {

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;
}