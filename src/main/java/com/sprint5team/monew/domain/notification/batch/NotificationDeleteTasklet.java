package com.sprint5team.monew.domain.notification.batch;

import com.google.common.annotations.VisibleForTesting;
import com.sprint5team.monew.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 확인된 알림 중 7일 이상 경과된 항목을 삭제하는 Tasklet 클래스
 * Spring Batch의 Step 에서 실행되어 삭제 로직을 수행한다
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationDeleteTasklet implements Tasklet {

    private final NotificationRepository notificationRepository;

    /**
     * Step 실행 시 호출되는 메서드로,
     * updatedAt 기준 7일 이상 지난 확인된 알림들을 삭제한다
     *
     * @param contribution 현재 실행 중인 Step의 상태를 제공하고 갱신할 수 있는 객체
     * @param chunkContext Step 및 Job의 실행 컨텍스트 정보를 포함한 객체
     * @return 작업 완료 상태 (RepeatStatus.FINISHED)
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        try {
            log.info("1주일 경과된 확인된 알림 삭제 시작");
            Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
            notificationRepository.deleteByConfirmedIsTrueAndUpdatedAtBefore(cutoff);
        } catch (Exception e) {
            log.error("알림 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
        return RepeatStatus.FINISHED;
    }

    /**
     * 테스트 용도의 메서드로, cutoffTime(7일) 이전에 확인된 알림들을 삭제한다
     *
     * @param cutoffTime 삭제 기준 시간
     */
    @VisibleForTesting
    public void deleteWithCutoffTime(Instant cutoffTime) {
        notificationRepository.deleteByConfirmedIsTrueAndUpdatedAtBefore(cutoffTime);
    }
}