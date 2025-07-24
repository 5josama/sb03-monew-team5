package com.sprint5team.monew.repository.comment;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.entity.Like;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.comment.repository.CommentRepositoryImpl;
import com.sprint5team.monew.domain.comment.repository.LikeRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Import({CommentRepositoryImpl.class, QuerydslConfig.class})
@DisplayName("댓글 Repository 단위 테스트")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    private Comment createComment(Article article, User user, String content) {
        Comment comment = new Comment(article, user, content);
        commentRepository.save(comment);
        return comment;
    }


    @Test
    void 신규_댓글_등록_성공() {
        //given
        Article article = new Article();
        User user = new User("<EMAIL>", "<NICKNAME>", "<PASSWORD>");
        String content = "테스트 댓글 입니다.";

        //when
        Comment save = commentRepository.save(createComment(article, user, content));

        //then
        assertThat(save).isNotNull();
        assertThat(save.getArticle()).isNotNull();
        assertThat(save.getUser()).isNotNull();
        assertThat(save.getContent()).isEqualTo("테스트 댓글 입니다.");
        assertThat(save.getIsDeleted()).isEqualTo(false);
        assertThat(save.getLikeCount()).isEqualTo(0);
    }

    @Test
    void 댓글_날짜_빠른_순으로_조회_성공() {
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Comment save2 = commentRepository.save(createComment(article, user, "테스트 내용2"));
        Comment save3 = commentRepository.save(createComment(article, user, "테스트 내용3"));

        // 저장된 댓글에 좋아요 수 업데이트
        save1.update(1L);   // 좋아요 1개
        save2.update(2L);   // 좋아요 2개
        save3.update(3L);   // 좋아요 3개

        // 업데이트된 댓글 저장
        save1 = commentRepository.save(save1);
        save2 = commentRepository.save(save2);
        save3 = commentRepository.save(save3);

        //when
        List<Comment> commentList = commentRepository.findCommentsWithCursor(article.getId(),null,null, PageRequest.of(0,3, Sort.Direction.ASC,"createdAt")); // 정렬기준 1. 날짜, 2. 좋아요 수 (save1,save2,save3 순으로 정렬되어야 함)

        //then
        assertThat(commentList).hasSize(3);
        assertThat(commentList.get(0).getContent()).isEqualTo(save1.getContent());
        assertThat(commentList.get(1).getContent()).isEqualTo(save2.getContent());
        assertThat(commentList.get(2).getContent()).isEqualTo(save3.getContent());
    }

    @Test
    void 댓글_좋아요수_많은_순으로_조회_성공() {
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Comment save2 = commentRepository.save(createComment(article, user, "테스트 내용2"));
        Comment save3 = commentRepository.save(createComment(article, user, "테스트 내용3"));

        // 저장된 댓글에 좋아요 수 업데이트
        save1.update(1L);   // 좋아요 1개
        save2.update(2L);   // 좋아요 2개
        save3.update(3L);   // 좋아요 3개

        // 업데이트된 댓글 저장
        save1 = commentRepository.save(save1);
        save2 = commentRepository.save(save2);
        save3 = commentRepository.save(save3);

        //when
        List<Comment> commentList = commentRepository.findCommentsWithCursor(
                article.getId(),
                null,
                null,
                PageRequest.of(0, 3, Sort.Direction.DESC, "likeCount")
        );

        //then
        assertThat(commentList).hasSize(3);
        assertThat(commentList.get(0).getContent()).isEqualTo("테스트 내용3");
        assertThat(commentList.get(0).getLikeCount()).isEqualTo(3L);
        assertThat(commentList.get(1).getContent()).isEqualTo("테스트 내용2");
        assertThat(commentList.get(1).getLikeCount()).isEqualTo(2L);
        assertThat(commentList.get(2).getContent()).isEqualTo("테스트 내용1");
        assertThat(commentList.get(2).getLikeCount()).isEqualTo(1L);
    }


    @Test
    void 댓글_물리삭제_성공(){
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Comment save2 = commentRepository.save(createComment(article, user, "테스트 내용2"));
        Comment save3 = commentRepository.save(createComment(article, user, "테스트 내용3"));

        // 저장된 댓글에 좋아요 수 업데이트
        save1.update(1L);   // 좋아요 1개
        save2.update(2L);   // 좋아요 2개
        save3.update(3L);   // 좋아요 3개

        // 업데이트된 댓글 저장
        save1 = commentRepository.save(save1);
        save2 = commentRepository.save(save2);
        save3 = commentRepository.save(save3);

        //when
        commentRepository.deleteById(save3.getId());    //save3 물리 삭제

        //then
        assertThat(commentRepository.findById(save3.getId())).isEmpty();
        assertThat(commentRepository.findAll()).hasSize(2);
    }
    @Test
    void 댓글_수정_성공(){
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Comment save2 = commentRepository.save(createComment(article, user, "테스트 내용2"));
        Comment save3 = commentRepository.save(createComment(article, user, "테스트 내용3"));

        // 저장된 댓글에 좋아요 수 업데이트
        save1.update(1L);   // 좋아요 1개
        save2.update(2L);   // 좋아요 2개
        save3.update(3L);   // 좋아요 3개

        // 업데이트된 댓글 저장
        save1 = commentRepository.save(save1);
        save2 = commentRepository.save(save2);
        save3 = commentRepository.save(save3);

        //when
        save3.update("수정된 테스트 내용");
        commentRepository.save(save3);    //save3 수정된 내용 저장

        //then
        assertThat(commentRepository.findById(save3.getId()).get().getContent()).isEqualTo("수정된 테스트 내용");
    }

    @Test
    void 댓글_좋아요_성공(){
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Like like;

        //when
        like = likeRepository.save(new Like(save1, user));
        save1.update(save1.getLikeCount() + 1);
        save1 = commentRepository.save(save1);

        //then
        assertThat(save1.getLikeCount()).isEqualTo(1);
        assertThat(like.getComment()).isEqualTo(save1);
        assertThat(like.getUser()).isEqualTo(user);
    }
    @Test
    void 댓글_좋아요_취소_성공(){
        //given
        Article article = new Article("SRC1", "http://example.com", "Title", "Summary", Instant.now());
        User user = new User("test@test.com", "nickname", "password");

        article = articleRepository.save(article);
        user = userRepository.save(user);

        // 댓글 생성 및 저장
        Comment save1 = commentRepository.save(createComment(article, user, "테스트 내용1"));
        Like like;

        //좋아요 설정
        like = likeRepository.save(new Like(save1, user));
        save1.update(save1.getLikeCount() + 1);
        save1 = commentRepository.save(save1);

        //when
        save1.update(save1.getLikeCount() - 1);
        save1 = commentRepository.save(save1);
        likeRepository.deleteById(like.getId());

        //then
        assertThat(save1.getLikeCount()).isEqualTo(0);
        assertThat(likeRepository.findById(like.getId())).isEmpty();
    }
}