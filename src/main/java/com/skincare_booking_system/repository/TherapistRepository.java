package com.skincare_booking_system.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.Therapist;

@Repository
public interface TherapistRepository extends JpaRepository<Therapist, Long> {
    boolean existsByUsername(String username);

    List<Therapist> findByStatusTrue();

    List<Therapist> findByStatusFalse();

    Optional<Therapist> findByPhone(String phone);

    List<Therapist> findByFullNameContainingIgnoreCase(String fullName);

    Optional<Therapist> findByUsername(String username);

    Therapist findTherapistById(Long id);

    @Query(
            value = "SELECT DISTINCT t.* FROM therapist t "
                    + "INNER JOIN therapist_schedule ts ON t.therapistid = ts.therapist_id "
                    + "INNER JOIN specific_therapist_schedule stsch ON ts.therapist_schedule_id = stsch.therapist_schedule_id "
                    + "WHERE ts.working_day = ?1 AND stsch.shift_id = ?2",
            nativeQuery = true)
    Set<Therapist> getTherapistForBooking(LocalDate date, long shiftId);
}
