package com.sprint5team.monew.domain.user.entity;

import com.sprint5team.monew.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Builder
@Entity
@Getter
@Table(name = "tbl_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 20, nullable = false)
    private String nickname;

    @Column(name = "password", length = 20, nullable = false)
    private String password;

    @CreatedDate
    @Column(name = "created_at", columnDefinition = "timestamp with time zone", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public User(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.isDeleted = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void softDelete() {
      this.isDeleted = true;
    }
}
