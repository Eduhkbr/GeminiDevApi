package com.eduhkbr.gemini.DevApi.repository;

import com.eduhkbr.gemini.DevApi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
