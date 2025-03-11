package com.skincare_booking_system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUsername(String username);

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    User findUserById(long id);

    Optional<User> findUserByEmail(String email);
}
