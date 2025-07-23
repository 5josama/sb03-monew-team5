package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTasklet implements Tasklet {

    private final NotificationService notificationService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("[NotificationTasklet] 알림 생성 Tasklet 시작");
        try {
            notificationService.notifyNewArticles();
            log.info("[NotificationTasklet] 알림 생성 Tasklet 완료");
        } catch (Exception e) {
            log.error("[NotificationTasklet] 알림 생성 중 오류 발생", e);
            throw e;
        }

        return RepeatStatus.FINISHED;
    }
}
