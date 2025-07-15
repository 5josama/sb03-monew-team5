package com.sprint5team.monew.integration.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint5team.monew.domain.article.dto.ArticleViewDto;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.article.service.ArticleService;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.service.CommentService;
import com.sprint5team.monew.domain.user.dto.UserDto;
import com.sprint5team.monew.domain.user.dto.UserRegisterRequest;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@DisplayName("Comment 통합 테스트")
public class CommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;


    @Test
    void 유저가_댓글을_등록하면_댓글_테이블에_저장되어야_한다() throws Exception{

        //Given
        UUID userId = UUID.randomUUID();
        String content = "테스트 댓글 입니다.";

        User user = new User("test@naver.com", "testname", "password1234");
        User createdUser = userRepository.save(user);

        Article article = new Article("Naver", "http://naver.com", "테스트 뉴스제목", "뉴스요약", Instant.now());
        Article createdArticle = articleRepository.save(article);

        CommentRegisterRequest request = new CommentRegisterRequest(createdArticle.getId(), createdUser.getId(), content);    // 테스트 댓글생성 요청
        String requestBody = objectMapper.writeValueAsString(request);

        //When && Then
        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id",notNullValue()))
                .andExpect(jsonPath("$.articleId",is(createdArticle.getId().toString())))
                .andExpect(jsonPath("$.userId",is(createdUser.getId().toString())))
                .andExpect(jsonPath("$.userNickname",is(createdUser.getNickname())))
                .andExpect(jsonPath("$.content",is(content)))
                .andExpect(jsonPath("$.likeCount",is(0)))
                .andExpect(jsonPath("$.likedByMe",is(false)));
    }
}
