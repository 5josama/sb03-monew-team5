package com.sprint5team.monew.domain.notification.service;

import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.comment.repository.CommentRepository;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.notification.dto.NotificationDto;
import com.sprint5team.monew.domain.notification.entity.Notification;
import com.sprint5team.monew.domain.notification.entity.ResourceType;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import com.sprint5team.monew.domain.user.entity.User;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final CommentRepository commentRepository;

    @Override
    public NotificationDto notifyArticleForInterest(UUID userId, UUID interestId, String interestName, int articleCount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Interest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new IllegalArgumentException("관심사를 찾을 수 없습니다."));

        String content = String.format("[%s]와 관련된 기사가 %d건 등록되었습니다.", interestName, articleCount);

        Notification notification = Notification.builder()
                .user(user)
                .interest(interest)
                .content(content)
                .resourceType(ResourceType.INTEREST)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationDto.from(saved);
    }

    @Override
    public NotificationDto notifyCommentLiked(UUID userId, UUID commentId, String likerName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        String content = likerName + "님이 내 댓글을 좋아했습니다.";

        Notification notification = Notification.builder()
                .user(user)
                .comment(comment)
                .content(content)
                .resourceType(ResourceType.COMMENT)
                .confirmed(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return NotificationDto.from(saved);
    }
}