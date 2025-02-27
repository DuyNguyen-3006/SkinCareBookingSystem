package com.skincare_booking_system.repository;

import com.skincare_booking_system.entity.Therapist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TherapistRepository extends JpaRepository<Therapist, String> {
    boolean existsByUsername(String username);
}
