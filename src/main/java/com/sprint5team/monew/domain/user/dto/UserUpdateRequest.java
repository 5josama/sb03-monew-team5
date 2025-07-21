package com.sprint5team.monew.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
    @NotBlank(message = "수정할 닉네임을 입력해 주세요.")
    @Size(min = 1, max = 20, message = "닉네임은 20자를 넘을 수 없습니다.")
    String nickname
) {

}
