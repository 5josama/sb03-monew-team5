
package com.sprint5team.monew.domain.comment.service;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.CommentDto;
import com.sprint5team.monew.domain.comment.dto.CommentRegisterRequest;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import com.sprint5team.monew.global.exception.ArticleNotFoundException;
import com.sprint5team.monew.global.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private UUID commentId;
    private Article article;
    private User user;
    private Instant createdAt;
    private String content;
    private CommentDto createdComment;
    private Comment comment;

    @BeforeEach
    void setUp() {
        commentId = UUID.randomUUID();
        article = new Article("Naver","http://naver.com/testURL","테스트 기사","테스트 기사 내용 요약", false, LocalDateTime.now());
        user = new User("test@test.com","테스트 사용자","test1234");
        createdAt = Instant.now();
        content = "테스트 댓글";
        comment = new Comment(article, user, content);
        ReflectionTestUtils.setField(comment, "id", commentId);
        ReflectionTestUtils.setField(comment, "createdAt", createdAt);
        createdComment = new CommentDto(commentId, article.getId(), user.getId(), user.getNickname(), content, (long) 0, false, createdAt);
    }

    @Test
    void 댓글_생성_성공_테스트(){
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(article.getId(), user.getId(), content);
        given(articleRepository.findById(article.getId())).willReturn(Optional.of(article));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        given(commentMapper.toDto(any(Comment.class))).willReturn(createdComment);

        //when
        CommentDto result = commentService.create(request);

        //then
        assertThat(result).isEqualTo(createdComment);
        verify(articleRepository).findById(article.getId());
        verify(userRepository).findById(user.getId());
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(any(Comment.class));
    }

    @Test
    void 댓글_생성_실패_존재하지않는_게시글(){
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(article.getId(), user.getId(), content);
        given(articleRepository.findById(article.getId())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> commentService.create(request))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다.");

        verify(articleRepository).findById(article.getId());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void 댓글_생성_실패_빈_내용(){
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(article.getId(), user.getId(), "");

        //when & then
        assertThatThrownBy(() -> commentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 내용이 비어있습니다.");

        verify(articleRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).findById(any(UUID.class));
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void 댓글_생성_실패_내용_길이초과(){
        //given
        String longContent = "a".repeat(1001); // 1000자 초과
        CommentRegisterRequest request = new CommentRegisterRequest(article.getId(), user.getId(), longContent);

        //when & then
        assertThatThrownBy(() -> commentService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("댓글 내용이 너무 깁니다. 최대 1000자까지 입력 가능합니다.");

        verify(articleRepository, never()).findById(any(UUID.class));
        verify(userRepository, never()).findById(any(UUID.class));
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }
}