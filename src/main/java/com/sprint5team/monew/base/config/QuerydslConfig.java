package com.sprint5team.monew.base.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PackageName  : com.sprint5team.monew.base.config
 * FileName     : QuerydslConfig
 * Author       : dounguk
 * Date         : 2025. 7. 13.
 */
@Configuration
public class QuerydslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        return new JPAQueryFactory(em);
    }
}
