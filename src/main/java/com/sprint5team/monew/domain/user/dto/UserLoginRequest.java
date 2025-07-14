package com.sprint5team.monew.domain.user.dto;

public record UserLoginRequest(

    String email,
    String password
) {
}
