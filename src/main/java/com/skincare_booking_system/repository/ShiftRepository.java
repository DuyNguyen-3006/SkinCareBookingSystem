package com.skincare_booking_system.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.skincare_booking_system.entities.Shift;

public interface ShiftRepository extends JpaRepository<Shift, Long> {
    Shift findByShiftId(long id);

    @Query(
            value = "select distinct s.* from shift s \n" + "inner join specific_therapist_schedule sts\n"
                    + "on s.shift_id = sts.shift_id\n"
                    + "inner join booking b \n"
                    + "on sts.therapist_schedule_id = b.therapist_schedule_id\n"
                    + "inner join slot sl\n"
                    + "on b.slot_id = sl.slotid\n"
                    + "where ((?1 between s.start_time and s.end_time) \n"
                    + "or (?2 between s.start_time and s.end_time)) and b.booking_id = ?3;",
            nativeQuery = true)
    List<Shift> getShiftForBooking(LocalTime timeStart, LocalTime timeEnd, long id);

    @Query(value = "select * from shift s order by s.end_time DESC LIMIT 1", nativeQuery = true)
    Shift getLatestShift();

}
