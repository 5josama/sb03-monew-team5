package com.sprint5team.monew.domain.article.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import com.sprint5team.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "tbl_article_count")
@Getter
@NoArgsConstructor
public class ArticleCount extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", columnDefinition = "timestamp with time zone")
    private Instant createdAt;

    public ArticleCount(Article article, User user) {
        this.article = article;
        this.user = user;
    }
}
