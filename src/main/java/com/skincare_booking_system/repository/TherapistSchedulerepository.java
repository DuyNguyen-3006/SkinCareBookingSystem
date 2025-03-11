package com.skincare_booking_system.repository;

import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.TherapistSchedule;

@Repository
public interface TherapistSchedulerepository extends JpaRepository<TherapistSchedule, Long> {
    @Query(
            value = "select * from therapist_schedule ts where ts.therapist_id = ?1 " + "and ts.working_day = ?2",
            nativeQuery = true)
    TherapistSchedule getTherapistScheduleId(long therapistId, LocalDate date);

    @Query(
            value = "SELECT DISTINCT ts.* FROM therapist_schedule ts " + "INNER JOIN specific_therapist_schedule sts "
                    + "ON ts.therapist_schedule_id = sts.therapist_schedule_id "
                    + "INNER JOIN therapist t "
                    + "ON ts.therapist_id = t.id "
                    + "WHERE ts.working_day = ?1",
            nativeQuery = true)
    List<TherapistSchedule> getTherapistScheduleByDay(LocalDate date);

    TherapistSchedule findByTherapistScheduleId(long id);

    @Query(value = "DELETE FROM specific_therapist_schedule WHERE therapist_schedule_id = ?1", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteSpecificSchedule(long id);
}
