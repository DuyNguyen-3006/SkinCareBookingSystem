package com.skincare_booking_system.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skincare_booking_system.entities.Therapist;
import org.springframework.stereotype.Repository;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Long> {
    boolean existsByUsername(String username);

    List<Therapist> findByStatusTrue();

    List<Therapist> findByStatusFalse();

    Optional<Therapist> findByPhone(String phone);

    List<Therapist> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Therapist> findByUsername(String username);

    Therapist findTherapistById(Long id);
}
