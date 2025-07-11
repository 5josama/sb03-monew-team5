package com.sprint5team.monew.domain.user.repository;

import com.sprint5team.monew.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {


}
