package com.sprint5team.monew.repository.comment;

import com.sprint5team.monew.base.config.QuerydslConfig;
import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@Import({CommentRepository.class, QuerydslConfig.class})
@DisplayName("댓글 Repository 단위 테스트")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

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
    void 댓글_날짜_좋아요수_순으로_조회_성공() {
        //given
        Article article1 = new Article("<SOURCE1>", "<SOURCEURL1>", "<TITLE1>", "<SUMMARY1>", Instant.now());
        User user1 = new User("<EMAIL1>", "<NICKNAME1>", "<PASSWORD1>");
        Comment comment1 = new Comment(article1, user1, "테스트 내용1");
        comment1.update((long) 1);   //좋아요 1개, 가장먼저 생성됨

        Article article2 = new Article("<SOURCE2>", "<SOURCEURL2>", "<TITLE2>", "<SUMMARY2>", Instant.now());
        User user2 = new User("<EMAIL2>", "<NICKNAME2>", "<PASSWORD2>");
        Comment comment2 = new Comment(article2, user2, "테스트 내용2");
        comment2.update((long) 2);   //좋아요 2개, 2번째로 생성됨

        Article article3 = new Article("<SOURCE3>", "<SOURCEURL3>", "<TITLE3>", "<SUMMARY3>", Instant.now());
        User user3 = new User("<EMAIL3>", "<NICKNAME3>", "<PASSWORD3>");
        Comment comment3 = new Comment(article3, user3, "테스트 내용3");
        comment3.update((long) 3);   //좋아요 3개, 3번째로 생성됨

        Comment save1 = commentRepository.save(createComment(comment1.getArticle(), comment1.getUser(), comment1.getContent()));
        Comment save2 = commentRepository.save(createComment(comment2.getArticle(), comment2.getUser(), comment2.getContent()));
        Comment save3 = commentRepository.save(createComment(comment3.getArticle(), comment3.getUser(), comment3.getContent()));

        //when
        //TODO findAll 수정필요
        List<Comment> commentList = commentRepository.findAll(); // 정렬기준 1. 날짜, 2. 좋아요 수 (save1,save2,save3 순으로 정렬되어야 함)

        //then
        assertThat(commentList).hasSize(3);
        assertThat(commentList.get(0).getContent()).isEqualTo(save1.getContent());
        assertThat(commentList.get(0).getLikeCount()).isEqualTo(save1.getLikeCount());
        assertThat(commentList.get(1).getContent()).isEqualTo(save2.getContent());
        assertThat(commentList.get(1).getLikeCount()).isEqualTo(save2.getLikeCount());
        assertThat(commentList.get(2).getContent()).isEqualTo(save3.getContent());
        assertThat(commentList.get(2).getLikeCount()).isEqualTo(save3.getLikeCount());
    }

    @Test
    void 댓글_좋아요수_날짜_순으로_조회_성공() {
        //given
        Article article1 = new Article("<SOURCE1>", "<SOURCEURL1>", "<TITLE1>", "<SUMMARY1>", Instant.now());
        User user1 = new User("<EMAIL1>", "<NICKNAME1>", "<PASSWORD1>");
        Comment comment1 = new Comment(article1, user1, "테스트 내용1");
        comment1.update((long) 1);   //좋아요 1개, 가장먼저 생성됨

        Article article2 = new Article("<SOURCE2>", "<SOURCEURL2>", "<TITLE2>", "<SUMMARY2>", Instant.now());
        User user2 = new User("<EMAIL2>", "<NICKNAME2>", "<PASSWORD2>");
        Comment comment2 = new Comment(article2, user2, "테스트 내용2");
        comment2.update((long) 2);   //좋아요 2개, 2번째로 생성됨

        Article article3 = new Article("<SOURCE3>", "<SOURCEURL3>", "<TITLE3>", "<SUMMARY3>", Instant.now());
        User user3 = new User("<EMAIL3>", "<NICKNAME3>", "<PASSWORD3>");
        Comment comment3 = new Comment(article3, user3, "테스트 내용3");
        comment3.update((long) 3);   //좋아요 3개, 3번째로 생성됨

        Comment save1 = commentRepository.save(createComment(comment1.getArticle(), comment1.getUser(), comment1.getContent()));
        Comment save2 = commentRepository.save(createComment(comment2.getArticle(), comment2.getUser(), comment2.getContent()));
        Comment save3 = commentRepository.save(createComment(comment3.getArticle(), comment3.getUser(), comment3.getContent()));

        //when
        //TODO findAll 수정필요
        List<Comment> commentList = commentRepository.findAll(); // 정렬기준 1. 좋아요수, 2. 날짜 (save3,save2,save1 순으로 정렬되어야 함)

        //then
        assertThat(commentList).hasSize(3);
        assertThat(commentList.get(0).getContent()).isEqualTo(save3.getContent());
        assertThat(commentList.get(0).getLikeCount()).isEqualTo(save3.getLikeCount());
        assertThat(commentList.get(1).getContent()).isEqualTo(save2.getContent());
        assertThat(commentList.get(1).getLikeCount()).isEqualTo(save2.getLikeCount());
        assertThat(commentList.get(2).getContent()).isEqualTo(save1.getContent());
        assertThat(commentList.get(2).getLikeCount()).isEqualTo(save1.getLikeCount());
    }
}