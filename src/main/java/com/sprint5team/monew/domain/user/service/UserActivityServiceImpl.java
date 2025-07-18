package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.entity.ArticleCount;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.user.dto.UserActivityDto;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import com.sprint5team.monew.domain.user_interest.entity.UserInterest;
import com.sprint5team.monew.domain.user_interest.mapper.UserInterestMapper;
import com.sprint5team.monew.domain.user_interest.repository.UserInterestRepository;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityServiceImpl {

  private final UserRepository userRepository;
  private final UserInterestRepository userInterestRepository;
  private final UserInterestMapper userInterestMapper;
  private final ArticleRepository articleRepository;
  private final ArticleCountRepository articleCountRepository;
  private final ArticleViewMapper articleViewMapper;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final CommentMapper commentMapper;

  public UserActivityDto getUserActivity(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    Set<UserInterest> userInterest = userInterestRepository.findByUserId(userId);
    List<Comment> comment = commentRepository.findByUserId(userId);
    List<Like> commentLike = likeRepository.findByUserId(userId);
    List<ArticleCount> articleCount = articleCountRepository.findAllByUserId(userId);

    List<SubscriptionDto> userInterests =
        userInterest.stream()
        .map(userInterestMapper::toDto)
        .toList();
    List<CommentDto> comments =
        comment.stream()
            .map(commentMapper::toDto)
            .toList();
    List<CommentLikeDto> commentLikes =
        commentLike.stream()
            .map(commentMapper::toDto)
            .toList();
    List<ArticleViewDto> articleViewDtos =
        articleCount.stream()
        .map(ac -> {
          Article article = articleRepository.findById(ac.getArticle().getId())
              .orElseThrow(ArticleNotFoundException::new);
          return articleViewMapper.toDto(article, user, ac);
        })
        .toList();

    UserActivityDto userActivityDto = new UserActivityDto(
        user.getId(),
        user.getEmail(),
        user.getNickname(),
        user.getCreatedAt(),
        userInterests,
        comments,
        commentLikes,
        articleViewDtos
    );

    return userActivityDto;
  }
}
