
package com.sprint5team.monew.service.comment;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.exception.ArticleNotFoundException;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.dto.*;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.exception.CommentNotFoundException;
import com.sprint5team.monew.domain.comment.mapper.CommentMapper;
import com.sprint5team.monew.domain.comment.mapper.LikeMapper;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.comment.service.CommentServiceImpl;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("댓글 Service 단위 테스트")
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private LikeMapper likeMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

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
        article = new Article("Naver", "http://naver.com/testURL", "테스트 기사", "테스트 기사 내용 요약", Instant.now());
        user = new User("test@test.com", "테스트 사용자", "test1234");
        createdAt = Instant.now();
        content = "테스트 댓글";
        comment = new Comment(article, user, content);
        ReflectionTestUtils.setField(comment, "id", commentId);
        ReflectionTestUtils.setField(comment, "createdAt", createdAt);
        createdComment = new CommentDto(commentId, article.getId(), user.getId(), user.getNickname(), content, (long) 0, false, createdAt);
    }

    @Test
    void 댓글_생성_성공_테스트() {
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
    void 댓글_생성_실패_존재하지않는_게시글() {
        //given
        CommentRegisterRequest request = new CommentRegisterRequest(article.getId(), user.getId(), content);
        given(articleRepository.findById(article.getId())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> commentService.create(request))
                .isInstanceOf(ArticleNotFoundException.class)
                .hasMessage("뉴스 기사 데이터 없음.");

        verify(articleRepository).findById(article.getId());
        verify(userRepository, never()).findById(any(UUID.class));
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void 댓글_페이지네이션을_사용한_생성일자순_조회_성공() {
        // given
        int pageSize = 3; // 페이지 크기를 3으로 설정, 실제로는 2개의 검색만 보이게 할 것임.
        Instant createdAt = Instant.now();
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 여러 댓글 생성 (페이지 사이즈보다 많게)
        Comment comment1 = new Comment(article, user, content + "1");
        Comment comment2 = new Comment(article, user, content + "2");
        Comment comment3 = new Comment(article, user, content + "3");

        comment1.update((long) 1);
        comment2.update((long) 2);
        comment3.update((long) 3);

        ReflectionTestUtils.setField(comment1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(comment2, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(comment3, "id", UUID.randomUUID());

        // 각 메시지에 해당하는 DTO 생성
        Instant message1CreatedAt = Instant.now().minusSeconds(30);
        Instant message2CreatedAt = Instant.now().minusSeconds(20);
        Instant message3CreatedAt = Instant.now().minusSeconds(10);

        ReflectionTestUtils.setField(comment1, "createdAt", message1CreatedAt);
        ReflectionTestUtils.setField(comment2, "createdAt", message2CreatedAt);
        ReflectionTestUtils.setField(comment3, "createdAt", message3CreatedAt);

        CommentDto commentDto1 = new CommentDto(
                comment1.getId(),
                article.getId(),
                user.getId(),
                user.getNickname(),
                comment1.getContent(),
                comment1.getLikeCount(),
                false,
                message1CreatedAt
        );

        CommentDto commentDto2 = new CommentDto(
                comment2.getId(),
                article.getId(),
                user.getId(),
                user.getNickname(),
                comment2.getContent(),
                comment2.getLikeCount(),
                false,
                message2CreatedAt
        );

        CommentDto commentDto3 = new CommentDto(
                comment3.getId(),
                article.getId(),
                user.getId(),
                user.getNickname(),
                comment3.getContent(),
                comment3.getLikeCount(),
                false,
                message3CreatedAt
        );


        // 첫 페이지 결과 세팅 (2개 메시지)
        List<Comment> firstPageComments = List.of(comment1, comment2, comment3);
        List<CommentDto> firstPageDtos = List.of(commentDto1, commentDto2);

        // 첫 페이지는 다음 페이지가 있고, 커서는 comment2의 생성 시간이어야 함
        CursorPageResponseCommentDto firstPageResponse = new CursorPageResponseCommentDto(
                firstPageDtos,
                message3CreatedAt.toString(),
                message3CreatedAt,
                pageSize - 1,
                3L,
                true
        );

        given(commentRepository.countTotalElements(any()))
                .willReturn(3L);
        given(commentRepository.findCommentsWithCursor(eq(article.getId()), eq(createdAt.toString()), eq(createdAt), any(Pageable.class)))
                .willReturn(firstPageComments);
        given(commentMapper.toDto(eq(comment1))).willReturn(commentDto1);
        given(commentMapper.toDto(eq(comment2))).willReturn(commentDto2);

        // when
        CursorPageResponseCommentDto result = commentService.find(article.getId(), createdAt.toString(), createdAt, pageable);

        // then
        assertThat(result).isEqualTo(firstPageResponse);
        assertThat(result.content()).hasSize(2);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.nextCursor()).isEqualTo(message3CreatedAt.toString());

        // 두 번째 페이지 테스트
        // given
        List<Comment> secondPageMessages = List.of(comment3);
        List<CommentDto> secondPageDtos = List.of(commentDto3);
        CursorPageResponseCommentDto secondPageResponse = new CursorPageResponseCommentDto(
                secondPageDtos,
                null,
                null,
                pageSize - 1,
                3L,
                false
        );


        // 두 번째 페이지 모의 객체 설정
        given(commentRepository.countTotalElements(eq(article.getId())))
                .willReturn(3L);
        given(commentRepository.findCommentsWithCursor(eq(article.getId()), eq(firstPageResponse.nextCursor()), eq(firstPageResponse.nextAfter()), any(Pageable.class)))
                .willReturn(secondPageMessages);
        given(commentMapper.toDto(eq(comment3))).willReturn(commentDto3);

        // when - 두 번째 페이지 요청 (첫 페이지의 커서 사용)
        CursorPageResponseCommentDto secondResult = commentService.find(article.getId(), message3CreatedAt.toString(), message3CreatedAt, pageable);

        // then - 두 번째 페이지 검증
        assertThat(secondResult).isEqualTo(secondPageResponse);
        assertThat(secondResult.content()).hasSize(1); // 마지막 페이지는 항목 1개만 있음
        assertThat(secondResult.hasNext()).isFalse(); // 더 이상 다음 페이지 없음

    }

    @Test
    void 댓글_논리_삭제_성공() {
        //given
        comment.softDelete(true);
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        commentService.softDelete(commentId);

        //then
        verify(commentRepository).findById(eq(commentId));
        verify(commentRepository).save(any(Comment.class));

    }

    @Test
    void 댓글_논리_삭제_실패_존재하지않는_댓글ID() {
        //given
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> commentService.softDelete(commentId))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글_물리_삭제_성공(){
        //given
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));

        //when
        commentService.hardDelete(commentId);

        //then
        verify(commentRepository).findById(eq(commentId));
        verify(commentRepository).deleteById(eq(commentId));
    }

    @Test
    void 댓글_물리_삭제_실패_존재하지않는_댓글_ID(){
        //given
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> commentService.hardDelete(commentId))
                .isInstanceOf(CommentNotFoundException.class);

    }

    @Test
    void 댓글_수정_성공(){
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
        CommentDto updatedComment = new CommentDto(commentId,article.getId(),user.getId(),user.getNickname(),"수정된 댓글",0L,false,createdAt);

        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        given(commentMapper.toDto(any(Comment.class))).willReturn(updatedComment);

        //when
        CommentDto result = commentService.update(commentId,request);

        //then
        verify(commentRepository).save(any(Comment.class));
        assertThat(result.content()).isEqualTo("수정된 댓글");
    }

    @Test
    void 댓글_수정_실패_존재하지않는_댓글_ID(){
        //given
        CommentUpdateRequest request = new CommentUpdateRequest("수정된 댓글");
        CommentDto updatedComment = new CommentDto(commentId,article.getId(),user.getId(),user.getNickname(),"수정된 댓글",0L,false,createdAt);

        given(commentRepository.findById(eq(commentId))).willReturn(Optional.empty());

        //when && then
        assertThatThrownBy(() -> commentService.update(commentId,request))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    void 댓글_좋아요_성공(){
        //given
        UUID userId = UUID.randomUUID();
        Like like = new Like(comment, user);
        CommentLikeDto commentLikeDto = new CommentLikeDto(UUID.randomUUID(),userId,Instant.now(),commentId,UUID.randomUUID(),UUID.randomUUID(),"nickName","댓글내용",1L,Instant.now());
        given(commentRepository.findById(eq(commentId))).willReturn(Optional.of(comment));
        given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));

        comment.update(comment.getLikeCount() + 1);

        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        given(likeRepository.save(any(Like.class))).willReturn(like);
        given(likeMapper.toDto(any(Like.class))).willReturn(commentLikeDto);
        ReflectionTestUtils.setField(like,"id",UUID.randomUUID());


        //when
        CommentLikeDto likeDto = commentService.like(commentId,userId);

        //then
        assertThat(likeDto).isEqualTo(commentLikeDto);
        verify(likeRepository).save(any(Like.class));
        verify(commentRepository).save(any(Comment.class));
        verify(likeMapper).toDto(any(Like.class));
        verify(commentRepository).findById(eq(commentId));
        verify(userRepository).findById(eq(userId));
    }

    @Test
    void 댓글_좋아요_취소_성공(){
        //given
        UUID userId = UUID.randomUUID();
        Like like = new Like(comment, user);
        ReflectionTestUtils.setField(like,"id",UUID.randomUUID());

        comment.update(comment.getLikeCount() - 1);
        given(commentRepository.findById(eq(comment.getId()))).willReturn(Optional.of(comment));
        given(userRepository.findById(eq(userId))).willReturn(Optional.of(user));
        given(likeRepository.findByUserIdAndCommentId(userId,commentId)).willReturn(Optional.of(like));
        willDoNothing().given(likeRepository).deleteById(eq(like.getId()));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);

        //when
        commentService.cancelLike(commentId,userId);

        //then
        verify(likeRepository).deleteById(like.getId());
        verify(commentRepository).save(any(Comment.class));
        verify(commentRepository).findById(eq(commentId));
        verify(userRepository).findById(eq(userId));
        verify(likeRepository).findByUserIdAndCommentId(eq(userId),eq(commentId));
    }


}