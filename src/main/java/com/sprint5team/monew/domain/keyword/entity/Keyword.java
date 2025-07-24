package com.sprint5team.monew.domain.keyword.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sprint5team.monew.base.entity.BaseEntity;
import com.sprint5team.monew.domain.interest.entity.Interest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

/**
 * PackageName  : com.sprint5team.monew.domain.keyword.entity
 * FileName     : Keyword
 * Author       : dounguk
 * Date         : 2025. 7. 9.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Getter
@Table(
    name = "tbl_keyword",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"interest_id", "name"})
    }
)
public class Keyword extends BaseEntity {

    @Column(name = "created_at", columnDefinition = "timestamp with time zone", nullable = false)
    @CreatedDate
    private Instant createdAt;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id", nullable = false)
    @JsonIgnore
    private Interest interest;

    public Keyword(String name, Interest interest) {
        this.name = name;
        this.interest = interest;
    }
}