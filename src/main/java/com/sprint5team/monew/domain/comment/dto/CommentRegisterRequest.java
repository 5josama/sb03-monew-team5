package com.sprint5team.monew.domain.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CommentRegisterRequest(

        @NotNull(message = "기사 ID는 필수 입니다.")
        UUID articleId,

        @NotNull(message = "사용자 Id는 필수 입니다.")
        UUID userId,

        @NotBlank(message = "댓글 내용을 입력해 주세요.")
        @Size(min = 1,max = 500,message = "댓글은 1자 이상 500자 이하로 작성해주세요.")
        String content
) {
}
