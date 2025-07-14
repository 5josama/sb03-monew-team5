package com.sprint5team.monew.domain.user.repository;

import com.sprint5team.monew.domain.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);

  User findByEmailAndPassword(String email, String password);
}
