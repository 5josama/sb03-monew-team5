package com.sprint5team.monew.domain.notification.entity;

import com.sprint5team.monew.base.entity.BaseUpdatableEntity;
import com.sprint5team.monew.domain.comment.entity.Comment;
import com.sprint5team.monew.domain.interest.entity.Interest;
import com.sprint5team.monew.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

/**
 * 사용자에게 전달되는 알림 엔티티
 * 알림은 구독한 관심사와 관련된 기사 생성 또는 댓글 좋아요에 의해 생성될 수 있으며, 확인 여부를 포함한다
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_notification")
public class Notification extends BaseUpdatableEntity {

    @Column(name = "resource_type", nullable = false, length = 8)
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "confirmed", nullable = false)
    private boolean confirmed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;

    public void confirm() {
        this.confirmed = true;
    }
}