package com.skincare_booking_system.repository;

import java.util.List;
import java.util.Optional;

import com.skincare_booking_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entity.Therapist;

public interface TherapistRepository extends JpaRepository<Therapist, String> {
    boolean existsByUsername(String username);

    List<Therapist> findByStatusTrue();

    List<Therapist> findByStatusFalse();

    Optional<Therapist> findByPhone(String phone);

    List<Therapist> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Therapist> findByUsername(String username);
}
