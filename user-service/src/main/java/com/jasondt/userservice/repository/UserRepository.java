package com.jasondt.userservice.repository;

import com.jasondt.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    List<User> findAllByDeletedFalse();
    Optional<User> findByIdAndDeletedFalse(UUID id);
}
