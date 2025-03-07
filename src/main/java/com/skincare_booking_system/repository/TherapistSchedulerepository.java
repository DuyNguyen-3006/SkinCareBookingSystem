package com.skincare_booking_system.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skincare_booking_system.entities.TherapistSchedule;

public interface TherapistSchedulerepository extends JpaRepository<TherapistSchedule, Long> {
    @Query(
            value = "select * from therapist_schedule ts where ts.therapist_id = ?1 " + "and ts.working_day = ?2",
            nativeQuery = true)
    TherapistSchedule getTherapistScheduleId(long therapistId, LocalDate date);
}
