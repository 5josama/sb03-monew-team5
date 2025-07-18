package com.sprint5team.monew.domain.article.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Table(name = "tbl_article")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Article extends BaseEntity {

    @Column(name = "source")
    private String source;

    @Column(name = "source_url", columnDefinition = "text")
    private String sourceUrl;

    @Column(name = "title", columnDefinition = "text")
    private String title;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "original_created_at", columnDefinition = "timestamp with time zone")
    private Instant originalDateTime;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp with time zone")
    private Instant createdAt;

    public Article(String source, String sourceUrl, String title, String summary, Instant originalDateTime) {
        this.source = source;
        this.sourceUrl = sourceUrl;
        this.title = title;
        this.summary = summary;
        this.originalDateTime = originalDateTime;
        this.isDeleted = false;
    }

    public void softDelete() {
        isDeleted = true;
    }
}
