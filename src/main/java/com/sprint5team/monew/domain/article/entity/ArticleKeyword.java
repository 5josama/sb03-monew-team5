package com.sprint5team.monew.domain.article.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint5team.monew.domain.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tbl_article_keyword")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ArticleKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    @JsonIgnore
    private Article article;

    @ManyToOne
    @JoinColumn(name = "interest_id", nullable = false)
    private Interest interest;

    public static ArticleKeyword of(Article article, Interest interest) {
        ArticleKeyword ak = new ArticleKeyword();
        ak.article = article;
        ak.interest = interest;
        article.getArticleKeywords().add(ak);
        return ak;
    }
}
