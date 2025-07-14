package com.sprint5team.monew.domain.user.dto;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeDto;
import com.sprint5team.monew.domain.interest.dto.InterestDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(
    UUID id,
    String email,
    String nickname,
    Instant createdAt,
    List<InterestDto> subscriptions,
    List<CommentDto> comments,
    List<CommentLikeDto> commentLikes,
    List<ArticleViewDto> articleViews
) {

}
