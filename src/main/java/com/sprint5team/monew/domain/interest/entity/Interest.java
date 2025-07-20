package com.sprint5team.monew.domain.interest.entity;

import com.sprint5team.monew.base.entity.BaseUpdatableEntity;
import com.sprint5team.monew.domain.keyword.entity.Keyword;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Keyword> keywords = new ArrayList<>();

    @OneToMany(mappedBy = "interest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> userInterests = new ArrayList<>();

    public Interest(String name) {
        this.name = name;
        this.subscriberCount = 0;
    }

    public void subscribed() {
        this.subscriberCount++;
    }
}