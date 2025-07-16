package com.sprint5team.monew.domain.article.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface ArticleCountCustomRepository {

    Map<UUID, Long> countViewByArticleIds(List<UUID> articleIds);

    Set<UUID> findViewedArticleIdsByUserId(UUID userId, List<UUID> articleIds);
}
