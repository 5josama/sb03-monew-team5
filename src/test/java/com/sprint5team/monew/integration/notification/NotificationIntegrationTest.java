package com.sprint5team.monew.integration.notification;

import com.sprint5team.monew.domain.article.entity.Article;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.notification.service.NotificationService;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NotificationIntegrationTest {

    @Autowired
    private NotificationService notificationService;
    @Autowired private UserRepository userRepository;
    @Autowired private CommentRepository commentRepository;
    @Autowired private InterestRepository interestRepository;
    @Autowired private NotificationRepository notificationRepository;

    private User user;
    private Interest interest;
    private Comment comment;

    @BeforeEach
    void setup() {
        user = userRepository.save(new User("user1", "test@abc.com", "1234"));
        interest = interestRepository.save(Interest.builder().name("경제").subscriberCount(0).build());
        comment = commentRepository.save(new Comment(mock(Article.class), user, "테스트 댓글"));
    }

    @Test
    void 관심사_기사등록_알림_생성_통합테스트() {
        // when
        NotificationDto result = notificationService.notifyArticleForInterest(user.getId(), interest.getId(), interest.getName(), 3);

        // then
        assertNotNull(result);
        assertEquals(user.getId(), result.userId());
        assertEquals(ResourceType.INTEREST, result.resourceType());
    }

    @Test
    void 댓글좋아요_알림_통합테스트() {
        // when
        NotificationDto result = notificationService.notifyCommentLiked(user.getId(), comment.getId(), "홍길동");

        // then
        assertNotNull(result);
        assertEquals(comment.getId(), result.resourceId());
        assertEquals(ResourceType.COMMENT, result.resourceType());
    }
}

