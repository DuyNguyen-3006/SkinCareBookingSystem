package com.skincare_booking_system.repository;

import com.skincare_booking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);
    Optional<User> findByPhone(String phone);
    Optional<User> findByUsername(String username);
}

