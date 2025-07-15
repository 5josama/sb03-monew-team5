package com.sprint5team.monew.domain.interest.entity;

import com.sprint5team.monew.base.entity.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.interest.entity
 * FileName     : Interest
 * Author       : dounguk
 * Date         : 2025. 7. 9.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@ToString
@Table(name = "tbl_interest")
public class Interest extends BaseUpdatableEntity {

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "name", nullable = false, length = 50)
    String name;

    @Column(name = "subscriber_count", nullable = false)
    long subscriberCount;

    public Interest(String name) {
        this.name = name;
        this.subscriberCount = 0;
    }
}