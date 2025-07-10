package com.sprint5team.monew.domain.article.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_article")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Article extends BaseEntity {

    @Column(name = "source")
    private String source;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "title")
    private String title;

    @Column(name = "summary")
    private String summary;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "original_created_at")
    private LocalDateTime originalDateTime;
}
