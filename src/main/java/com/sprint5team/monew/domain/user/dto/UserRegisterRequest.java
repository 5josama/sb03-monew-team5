package com.sprint5team.monew.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "회원가입 정보")
public record UserRegisterRequest(

        @Email(message = "잘못된 이메일 형식입니다.")
        @Schema(description = "가입 이메일", requiredMode = Schema.RequiredMode.REQUIRED)
        String email,

        @NotBlank(message = "사용할 닉네임을 입력해주세요.")
        @Size(min = 1, max = 20, message = "닉네임은 20자를 넘을 수 없습니다.")
        @Schema(description = "가입 닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
        String nickname,

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하여야 합니다.")
        @Schema(description = "가입 비밀번호", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
