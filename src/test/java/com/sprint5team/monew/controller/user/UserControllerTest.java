package com.sprint5team.monew.controller.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.user.controller.UserController;
import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
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

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").value(userId.toString()))
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

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("이메일 형식이 올바르지 않습니다."));
  }

  @Test
  void 사용자_회원가입_실패_닉네임_길이_제한_초과() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "가나다라마바사아자차카타파하1234567", // 닉네임 허용 길이 초과
        "test1234"
    );

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("닉네임은 20자 이하로 입력해주세요."));
  }

  @Test
  void 사용자_회원가입_실패_비밀번호_정책_위반() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "가나다라",
        "1234" // 비밀번호 최소 길이 위반
    );

    // when & then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("비밀번호는 6자리 이상 20자리 이하로 입력해주세요."));
  }
}
