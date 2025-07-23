package com.sprint5team.monew.base.util;

import com.sprint5team.monew.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

/**
 * ArticleScraperJobConfig 배치 잡에서 사용되는 알림 생성 Tasklet 클래스
 * 해당 Tasklet은 최근 등록된 기사와 관련된 관심사를 구독 중인 사용자에게
 * 알림을 생성하는 배치 작업을 수행한다
 *
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationTasklet implements Tasklet {

    private final NotificationService notificationService;

    /**
     * Spring Batch에서 Tasklet 기반 Step 실행 시 호출되는 핵심 메서드로,
     * 단일 작업 단위를 처리하고 완료 상태를 반환합니다.
     *
     * @param contribution 현재 실행 중인 Step의 상태를 제공하고 갱신할 수 있는 객체
     * @param chunkContext Step 및 Job의 실행 컨텍스트 정보를 포함한 객체
     * @return 작업 완료 상태 (RepeatStatus.FINISHED)
     * @throws RuntimeException 알림 생성 중 발생한 예외
     */
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
