package com.sprint5team.monew.domain.comment.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import com.sprint5team.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "tbl_like")
@Getter
@NoArgsConstructor
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

    public Like(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
    }
}
