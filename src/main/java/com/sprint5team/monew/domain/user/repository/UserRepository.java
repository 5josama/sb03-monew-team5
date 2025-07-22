package com.sprint5team.monew.domain.user.repository;

import com.sprint5team.monew.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);
  boolean existsByEmail(String email);

  Optional<User> findByEmailAndPassword(String email, String password);

  void deleteById(UUID id);

  void softDeleteById(UUID id);
}
