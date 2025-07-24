package com.sprint5team.monew.domain.comment.util;

import com.sprint5team.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentLikedEventListener {

    private final NotificationService notificationService;

    /**
     * CommentService 레이어와 NotificationService 레이어의 결합도를 낮추기 위해 이벤트 기반 아키텍쳐로 구현
     * @param event 좋아요 알림 이벤트
     */
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)                  // 알림 전송은 별도 트랜잭션(REQUIRES_NEW)으로 실행 하여 알림 생성 실패시에도 댓글 좋아요는 남아있게함.
    public void handleCommentLikedEvent(CommentLikedEvent event) {
        try{
            notificationService.notifyCommentLiked(
                    event.getCommentOwnerId(),
                    event.getCommentId(),
                    event.getLikerNickname()
            );
        }catch (Exception e){
            log.error("좋아요 알림 이벤트 실패");
        }
    }
}
