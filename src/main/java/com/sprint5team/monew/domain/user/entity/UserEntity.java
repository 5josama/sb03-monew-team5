package com.sprint5team.monew.domain.user.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
@Table(name = "tbl_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 20, nullable = false)
    private String password;

    @CreatedDate
    @Column(columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
    private Instant createdAt;

    private Boolean is_deleted;

    public UserEntity(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.is_deleted = false;
        this.createdAt = Instant.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
