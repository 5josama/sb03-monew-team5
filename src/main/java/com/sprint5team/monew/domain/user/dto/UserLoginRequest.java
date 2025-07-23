package com.sprint5team.monew.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "로그인 정보")
public record UserLoginRequest(

    @Email(message = "잘못된 이메일 형식입니다.")
    @Schema(description = "로그인 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    String email,

    @Schema(description = "로그인 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
    String password
) {
}
