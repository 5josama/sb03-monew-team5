package com.sprint5team.monew.domain.comment;

import com.sprint5team.monew.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

public class Like extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
    private Instant createdAt;
}
