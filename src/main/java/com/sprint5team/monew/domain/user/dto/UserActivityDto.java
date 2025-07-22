package com.sprint5team.monew.domain.user.dto;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<SubscriptionDto> subscriptions,
    List<CommentActivityDto> comments,
    List<CommentLikeActivityDto> commentLikes,
    List<ArticleViewDto> articleViews
) {

}
