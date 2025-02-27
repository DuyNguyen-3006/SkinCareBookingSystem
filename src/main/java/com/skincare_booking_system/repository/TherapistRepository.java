package com.skincare_booking_system.repository;

import com.skincare_booking_system.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TherapistRepository extends JpaRepository<Therapist, String> {
    boolean existsByUsername(String username);
    List<Therapist> findByStatusTrue();
    List<Therapist> findByStatusFalse();

    Optional<Therapist> findByPhone(String phone);

    List<Therapist> findByFullNameContainingIgnoreCase(String fullName);

}
