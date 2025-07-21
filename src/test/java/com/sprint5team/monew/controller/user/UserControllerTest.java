package com.sprint5team.monew.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.user.controller.UserController;
import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserLoginRequest;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.dto.UserUpdateRequest;
import com.sprint5team.monew.domain.user.exception.InvalidLoginException;
import com.sprint5team.monew.domain.user.exception.UserNotFoundException;
import com.sprint5team.monew.domain.user.service.UserServiceImpl;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserController.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserServiceImpl userService;

  @Test
  void 사용자_회원가입_성공() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "test",
        "test1234"
    );

    UUID userId = UUID.randomUUID();

    UserDto userDto = new UserDto(
        userId,
        "test@test.kr",
        "test",
        Instant.now()
    );

    given(userService.register(any(UserRegisterRequest.class)))
        .willReturn(userDto);

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.nickname").value("test"))
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(content().json(objectMapper.writeValueAsString(userDto)));
  }

  @Test
  void 사용자_회원가입_실패_이메일_형식_위반() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test", // 이메일 형식 위반
        "가나다라",
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_회원가입_실패_닉네임_길이_제한_초과() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "가나다라마바사아자차카타파하1234567", // 닉네임 허용 길이 초과
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_회원가입_실패_비밀번호_정책_위반() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "가나다라",
        "1234" // 비밀번호 최소 길이 위반
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_로그인_성공() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "test@test.kr",
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users/login")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  void 사용자_로그인_실패() throws Exception {
    // given
    UserLoginRequest request = new UserLoginRequest(
        "test@test.kr",
        "wrongpassword"
    );
    given(userService.login(any(String.class), any(String.class))).willThrow(new InvalidLoginException());

    // when and then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void 사용자_정보_수정_성공() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    String email = "test@test.kr";
    UserUpdateRequest request = new UserUpdateRequest("newNickname");
    UserDto userDto = new UserDto(userId, email, "newNickname", Instant.now());
    given(userService.update(any(UUID.class), any(UserUpdateRequest.class))).willReturn(userDto);

    // when and then
    mockMvc.perform(patch("/api/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(userId.toString()))
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.nickname").value("newNickname"))
        .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  void 사용자_정보_수정_실패_존재하지_않는_사용자() throws Exception {
    // given
    UUID userId = UUID.randomUUID();
    UserUpdateRequest request = new UserUpdateRequest("newNickname");
    given(userService.update(any(UUID.class), any(UserUpdateRequest.class))).willThrow(
        UserNotFoundException.class);

    // when and then
    mockMvc.perform(patch("/api/users/{userId}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound());
  }

}
