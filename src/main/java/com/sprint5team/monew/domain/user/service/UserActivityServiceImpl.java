package com.sprint5team.monew.domain.user.service;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.mapper.ArticleViewMapper;
import com.sprint5team.monew.domain.article.repository.ArticleCountRepository;
import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
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
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserActivityServiceImpl implements UserActivityService{

  private final UserRepository userRepository;
  private final UserInterestRepository userInterestRepository;
  private final ArticleViewMapper articleViewMapper;
  private final ArticleCountRepository articleCountRepository;
  private final UserInterestMapper userInterestMapper;
  private final CommentRepository commentRepository;
  private final LikeRepository likeRepository;
  private final CommentMapper commentMapper;

  @Override
  public UserActivityDto getUserActivity(UUID userId) {
    User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    List<UserInterest> userInterest = userInterestRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    List<Comment> comment = commentRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);
    List<Like> commentLike = likeRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId);

    List<SubscriptionDto> userInterests =
        userInterest.stream()
        .map(userInterestMapper::toDto)
        .toList();
    List<CommentActivityDto> comments =
        comment.stream()
            .map(commentMapper::toActivityDto)
            .toList();
    List<CommentLikeActivityDto> commentLikes =
        commentLike.stream()
            .map(commentMapper::toActivityDto)
            .toList();
    List<ArticleViewDto> articleViewDtos =
        articleCountRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId).stream()
            .map(ac -> {
              Article article = ac.getArticle();
              long viewCount = articleCountRepository.countViewByArticleId(article.getId());
              long commentCount = commentRepository.countByArticleId(ac.getArticle().getId());
              return articleViewMapper.toDto(article, user, ac, viewCount, commentCount);
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
