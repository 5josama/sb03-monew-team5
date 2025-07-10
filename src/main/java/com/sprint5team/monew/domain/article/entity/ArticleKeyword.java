package com.sprint5team.monew.domain.article.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_article_keyword")
@AllArgsConstructor
@NoArgsConstructor
public class ArticleKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @ManyToOne
    @JoinColumn(name = "interest_id")
    private ArticleKeyword articleKeyword;
}
