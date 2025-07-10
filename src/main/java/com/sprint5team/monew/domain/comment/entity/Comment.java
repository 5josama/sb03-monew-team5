package com.sprint5team.monew.domain.comment.entity;

import com.sprint5team.monew.base.entity.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "tbl_comment")
@Getter
public class Comment extends BaseUpdatableEntity {

    @CreatedDate
    @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name="content")
    private String content;

    @Column(name="is_deleted")
    private Boolean isDeleted;

    @Column(name="like_count")
    private Long likeCount;

    public void update(String content){
        this.content = content;
    }

    public void update(Long likeCount){
        this.likeCount = likeCount;
    }


}
