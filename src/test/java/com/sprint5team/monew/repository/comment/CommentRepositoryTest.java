package com.sprint5team.monew.repository.comment;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
@DisplayName("댓글 Repository 단위 테스트")
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    private Comment createComment(Article article, User user, String content ) {
        Comment comment = new Comment(article, user, content);
        commentRepository.save(comment);
        return comment;
    }


    @Test
    void 신규_댓글_등록_성공(){
        //given
        Article article = new Article();
        User user = new User("<EMAIL>","<NICKNAME>","<PASSWORD>");
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
}
