package com.sprint5team.monew.domain.article.service;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.dto.CursorPageFilter;
import com.sprint5team.monew.domain.article.dto.CursorPageResponseArticleDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleCountRepository articleCountRepository;
    private final UserRepository userRepository;
    private final ArticleViewMapper articleViewMapper;

    @Override
    public ArticleViewDto saveArticleView(UUID articleId, UUID userId) {
        Article article = articleRepository.findById(articleId).orElseThrow(ArticleNotFoundException::new);

        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("User id " + userId + " not found")
        );

        Optional<ArticleCount> articleCount = articleCountRepository.findByUserIdAndArticleId(userId, articleId);
        if (articleCount.isEmpty()) {
            articleCountRepository.save(new ArticleCount(article,  user));
        }

        return articleViewMapper.toDto(article, user, articleCount.orElse(null));
    }

    @Override
    public CursorPageResponseArticleDto getArticles(CursorPageFilter filter) {
        return null;
    }
}
