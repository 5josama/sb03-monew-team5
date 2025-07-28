package com.sprint5team.monew.base.aop;

import com.sprint5team.monew.base.metric.MonewMetrics;
import com.sprint5team.monew.base.service.BatchStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final MonewMetrics monewMetrics;
    private final BatchStatusService batchStatusService;

    @AfterReturning("execution(* com.sprint5team.monew.domain.user.service.UserService.register(..))")
    public void countUserCreated() {
        monewMetrics.getUserCreatedCounter().increment();
        monewMetrics.updateGaugeMetrics();
    }

    // 관심사 생성 성공 시 카운터 증가
    @AfterReturning("execution(* com.sprint5team.monew.domain.interest.service.InterestService.registerInterest(..))")
    public void countInterestCreated() {
        monewMetrics.getInterestCreatedCounter().increment();
        monewMetrics.updateGaugeMetrics();
    }

    // 뉴스 수집 완료 후 기사 개수 카운팅
    @AfterReturning(
            pointcut = "execution(* com.sprint5team.monew.domain.article.service.ArticleScraper.scrapeAll(..))",
            returning = "xml"
    )
    public void countArticleCreated(Object xml) {
        monewMetrics.updateGaugeMetrics();
    }

    @AfterReturning("execution(* com.sprint5team.monew.domain.article.util.*Scheduler.run*Job(..))")
    public void batchMetricsUpdated() {
        monewMetrics.updateBatchMetrics(batchStatusService);
    }
}
