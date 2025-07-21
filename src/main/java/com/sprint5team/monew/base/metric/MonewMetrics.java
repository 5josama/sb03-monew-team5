package com.sprint5team.monew.base.metric;

import com.sprint5team.monew.base.service.BatchStatusService;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class MonewMetrics {

    private final MeterRegistry meterRegistry;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final InterestRepository interestRepository;
    private final BatchStatusService batchStatusService;

    @Getter
    private Counter articleCreatedCounter;
    @Getter
    private Counter userCreatedCounter;
    @Getter
    private Counter interestCreatedCounter;

    private final AtomicLong totalArticlesGauge = new AtomicLong(0);
    private final AtomicLong activeUsersGauge = new AtomicLong(0);
    private final AtomicLong totalInterestsGauge = new AtomicLong(0);
    private final AtomicLong lastJobSuccessful = new AtomicLong(0);
    private final AtomicLong lastJobTimestamp = new AtomicLong(0);
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);
    private final AtomicLong lastExecutionTime = new AtomicLong(0);


    public MonewMetrics(MeterRegistry meterRegistry, ArticleRepository articleRepository, UserRepository userRepository, InterestRepository interestRepository, BatchStatusService batchStatusService) {
        this.meterRegistry = meterRegistry;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.interestRepository = interestRepository;
        this.batchStatusService = batchStatusService;
    }

    @PostConstruct
    public void initMetrics() {

        articleCreatedCounter = Counter.builder("monew.article.created")
                .description("생성된 뉴스 기사 수")
                .tag("system", "monew")
                .register(meterRegistry);

        userCreatedCounter = Counter.builder("monew.user.created")
                .description("생성된 유저 수")
                .tag("system", "monew")
                .register(meterRegistry);

        interestCreatedCounter = Counter.builder("monew.interest.created")
                .description("생성된 관심사 수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("library.books.total", this, MonewMetrics::getTotalArticles)
                .description("전체 뉴스 기사 수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("library.users.total", this, MonewMetrics::getActiveUsers)
                .description("전체 유저 수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("library.interests.total", this, MonewMetrics::getTotalInterests)
                .description("전체 관심사 수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("batch.last.success", lastJobSuccessful, AtomicLong::get)
                .description("배치 성공 여부 (1: 성공, 0: 실패)")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("batch.last.timestamp", lastJobTimestamp, AtomicLong::get)
                .description("마지막 배치 시각 (Epoch millis)")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("batch.success.count", successCount, AtomicLong::get)
                .description("배치 성공 횟수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("batch.failure.count", failureCount, AtomicLong::get)
                .description("배치 실패 횟수")
                .tag("system", "monew")
                .register(meterRegistry);

        Gauge.builder("batch.execution.time", lastExecutionTime, AtomicLong::get)
                .description("배치 실행 시간 (ms)")
                .tag("system", "monew")
                .register(meterRegistry);
    }

    public double getTotalArticles() {
        return totalArticlesGauge.get();
    }

    public double getActiveUsers() {
        return activeUsersGauge.get();
    }

    public double getTotalInterests() {
        return totalInterestsGauge.get();
    }

    public void updateGaugeMetrics() {

        updateTotalArticles();
        updateTotalUsers();
        updateTotalInterests();

        log.debug("[s4][MonewMetrics] 게이지 메트릭 업데이트 완료");
    }

    public void updateBatchMetrics(BatchStatusService statusService) {
        try {
            lastJobSuccessful.set(statusService.wasLastJobSuccessful() ? 1 : 0);
            lastJobTimestamp.set(statusService.getLastJobTime().toEpochMilli());
            successCount.set(statusService.getSuccessCount());
            failureCount.set(statusService.getFailureCount());
            lastExecutionTime.set(statusService.getTotalExecutionTime().toMillis());
            log.debug("[s4][MonewMetrics] 배치 메트릭 업데이트 완료");
        } catch (Exception e) {
            log.warn("[s4][MonewMetrics] 배치 메트릭 업데이트 중 오류 발생", e);
        }
    }

    private void updateTotalArticles() {

        try {
            long count = articleRepository.count();
            totalArticlesGauge.set(count);
        } catch (Exception e) {
            log.warn("[s4][MonewMetrics] 뉴스 기사 수 업데이트 중 오류 발생", e);
        }
    }

    private void updateTotalUsers() {

        try {
            long count = userRepository.count();
            activeUsersGauge.set(count);
        } catch (Exception e) {
            log.warn("[s4][MonewMetrics] 유저 수 업데이트 중 오류 발생", e);
        }
    }

    private void updateTotalInterests() {

        try {
            long count = interestRepository.count();
            totalInterestsGauge.set(count);
        } catch (Exception e) {
            log.warn("[s4][MonewMetrics] 관심사 수 업데이트 중 오류 발생", e);
        }
    }
}
