package com.sprint5team.monew.domain.user.dto;

import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.comment.dto.CommentActivityDto;
import com.sprint5team.monew.domain.comment.dto.CommentLikeActivityDto;
import com.sprint5team.monew.domain.user_interest.dto.SubscriptionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record UserActivityDto(

    @Schema(description = "사용자 ID")
    UUID id,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "닉네임")
    String nickname,

    @Schema(description = "가입한 날짜")
    Instant createdAt,

    @Schema(description = "구독 정보")
    List<SubscriptionDto> subscriptions,

    @Schema(description = "최근 작성한 댓글 (최대 10건)")
    List<CommentActivityDto> comments,

    @Schema(description = "최근 좋아요를 누른 댓글 (최대 10건)")
    List<CommentLikeActivityDto> commentLikes,

    @Schema(description = "최근 본 기사 (최대 10건)")
    List<ArticleViewDto> articleViews
) {

}
