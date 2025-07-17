package com.sprint5team.monew.base.health;

import com.sprint5team.monew.base.service.BatchStatusService;
import com.sprint5team.monew.domain.article.repository.ArticleRepository;
import com.sprint5team.monew.domain.interest.repository.InterestRepository;
import com.sprint5team.monew.domain.user.repository.UserRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("monew")
public class MonewHealthIndicator implements HealthIndicator {

    private final BatchStatusService  batchStatusService;
    private final ArticleRepository articleRepository;
    private final InterestRepository interestRepository;
    private final UserRepository userRepository;

    public MonewHealthIndicator(BatchStatusService batchStatusService, ArticleRepository articleRepository, InterestRepository interestRepository, UserRepository userRepository) {
        this.batchStatusService = batchStatusService;
        this.articleRepository = articleRepository;
        this.interestRepository = interestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Health health() {
        boolean lastJobSuccessful = batchStatusService.wasLastJobSuccessful();

        Health.Builder builder = lastJobSuccessful ? Health.up() : Health.down();

        builder.withDetail("batch", lastJobSuccessful ? "Last job success" : "Last job failed")
                .withDetail("timestamp", batchStatusService.getLastJobTime())
                .withDetail("successCount", batchStatusService.getSuccessCount())
                .withDetail("failureCount", batchStatusService.getFailureCount())
                .withDetail("totalExecutionTime", batchStatusService.getTotalExecutionTime().toString())
                .withDetail("lastJobSuccessful", lastJobSuccessful)
                .withDetail("totalUser", userRepository.count())
                .withDetail("totalInterest", interestRepository.count())
                .withDetail("totalArticle", articleRepository.count());

        if (!lastJobSuccessful) {
            builder.withDetail("lastFailureReason", batchStatusService.getLastFailureReason());
        }

        return builder.build();
    }
}
