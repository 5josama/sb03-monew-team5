package com.sprint5team.monew.integration.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.service.UserServiceImpl;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserServiceImpl userService;

  @Autowired
  private EntityManager entityManager;

  @Test
  void 사용자_등록_API_통합_테스트() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "test",
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.userId").isNotEmpty())
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.nickname").value("test"))
        .andExpect(jsonPath("$.createdAt").exists());
  }

  @Test
  void 사용자_등록_API_실패_통합_테스트_유효하지_않은_이메일_형식() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test", // 이메일 형식 위반
        "test",
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_등록_API_실패_통합_테스트_유효하지_않은_닉네임() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "testtesttesttesttesttest", // 닉네임 최대 길이 초과
        "test1234"
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_등록_API_실패_통합_테스트_유효하지_않은_비밀번호() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "test",
        "test" // 비밀번호 최소 길이 위반
    );

    // when and then
    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void 사용자_로그인_API_통합_테스트() throws Exception {
    // given
    UserRegisterRequest request = new UserRegisterRequest(
        "test@test.kr",
        "test",
        "test" // 비밀번호 최소 길이 위반
    );
    userService.register(request);

    entityManager.flush();
    entityManager.clear();

    // when and then
    mockMvc.perform(post("/api/users/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.createdAt").exists())
        .andExpect(jsonPath("$.email").value("test@test.kr"))
        .andExpect(jsonPath("$.userId").isNotEmpty())
        .andExpect(jsonPath("$.nickname").value("test"));
  }
}
