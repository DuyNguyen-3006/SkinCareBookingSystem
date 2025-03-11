package com.skincare_booking_system.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.skincare_booking_system.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingByBookingId(long id);

    @Query(
            value = "select distinct b.* from booking b\n" + "inner join specific_therapist_schedule stsch\n"
                    + "on b.therapist_schedule_id = stsch.therapist_schedule_id\n"
                    + "inner join shift s \n"
                    + "on stsch.shift_id = s.shift_id\n"
                    + "inner join slot sl\n"
                    + "on b.slot_id = sl.slotid\n"
                    + "where b.therapist_schedule_id = ?1 and b.slot_id = ?2 and b.status = 'PENDING';",
            nativeQuery = true)
    List<Booking> getBookingsByTherapistScheduleAndSlotId(long therapistScheduleId, long slotId);

    @Query(
            value = "UPDATE booking_detail SET price = ?1 WHERE booking_id = ?2  and service_id= ?3;",
            nativeQuery = true)
    @Transactional
    @Modifying
    void updateBookingDetail(double price, long bookingId, long serviceId);

    @Query(
            value = "UPDATE booking_package SET packageFinalPrice = ?1 WHERE booking_id = ?2 AND package_id = ?3;",
            nativeQuery = true)
    @Transactional
    @Modifying
    void updateBookingDetailForPackage(double packageFinalPrice, long bookingId, long packageId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM booking_detail WHERE booking_id = ?1 ", nativeQuery = true)
    void deleteBookingDetail(long id);

    @Query(
            value = "SELECT DISTINCT b.* FROM booking b " + "INNER JOIN specific_therapist_schedule stsch "
                    + "ON b.therapist_schedule_id = stsch.therapist_schedule_id "
                    + "INNER JOIN shift s "
                    + "ON stsch.shift_id = s.shift_id "
                    + "INNER JOIN slot sl "
                    + "ON b.slot_id = sl.slotid "
                    + "WHERE b.therapist_schedule_id = ?1 "
                    + "AND b.slot_id = ?2 "
                    + "AND b.status = 'PENDING';",
            nativeQuery = true)
    List<Booking> getBookingsByTherapistScheduleAndShiftId(long therapistScheduleId, long slotId);

    @Query("SELECT b FROM Booking b " + "JOIN b.therapistSchedule ts "
            + "WHERE ts.therapist.id = :therapistId AND b.status = 'COMPLETED' "
            + "AND FUNCTION('YEAR', b.bookingDay) = :year "
            + "AND FUNCTION('MONTH', b.bookingDay) = :month")
    List<Booking> findBookingByTherapistIdAndMonthYear(
            @Param("therapistId") Long therapistId, @Param("month") int month, @Param("year") int year);

    @Query(
            value = "SELECT b.* FROM booking b " + "INNER JOIN therapist_schedule tsch "
                    + "ON b.therapist_schedule_id = tsch.therapist_schedule_id "
                    + "WHERE tsch.working_day = ?1 AND tsch.therapist_id = ?2 AND b.status != 'CANCELLED' "
                    + "ORDER BY b.slot_id DESC",
            nativeQuery = true)
    List<Booking> getBookingsByTherapistInDay(LocalDate date, long therapistId);

    @Query(
            value = "SELECT b.* FROM booking b " + "INNER JOIN therapist_schedule tsch "
                    + "ON b.therapist_schedule_id = tsch.therapist_schedule_id "
                    + "WHERE tsch.working_day = ?1 "
                    + "AND tsch.therapist_id = ?2 "
                    + // Đổi account_id thành therapist_id
                    "AND b.status != 'CANCELLED' "
                    + "AND b.booking_id != ?3 "
                    + "ORDER BY b.slot_id DESC",
            nativeQuery = true)
    List<Booking> getBookingsByTherapistInDayForUpdate(LocalDate date, long therapistId, long bookingId);

    @Query(
            value = "SELECT b.* FROM booking b\n" + "INNER JOIN slot sl ON b.slot_id = sl.slotid\n"
                    + "INNER JOIN therapist_schedule ts ON b.therapist_schedule_id = ts.therapist_schedule_id\n"
                    + "WHERE ts.therapist_id = ?1 AND sl.slottime > ?2 AND b.booking_day = ?3\n"
                    + "AND b.status != 'CANCELLED'\n"
                    + "LIMIT 1",
            nativeQuery = true)
    Booking bookingNearestOverTime(long therapistId, LocalTime time, LocalDate date);

    @Query(
            value = "SELECT b.* FROM booking b\n" + "INNER JOIN slot sl ON b.slot_id = sl.slotid\n"
                    + "INNER JOIN therapist_schedule ts ON b.therapist_schedule_id = ts.therapist_schedule_id\n"
                    + "WHERE ts.therapist_id = ?1 AND sl.slottime < ?2 AND b.booking_day = ?3\n"
                    + "AND b.status != 'CANCELLED'\n"
                    + "LIMIT 1",
            nativeQuery = true)
    Booking bookingNearestBeforeTime(long therapistId, LocalTime time, LocalDate date);

    @Query(
            value = "SELECT b.* FROM booking b\n" + "INNER JOIN slot s ON b.slot_id = s.slotid\n"
                    + "INNER JOIN therapist_schedule ts ON b.therapist_schedule_id = ts.therapist_schedule_id\n"
                    + "WHERE s.slotid = ?1 AND ts.therapist_id = ?2 AND ts.working_day = ?3\n"
                    + "AND b.status != 'CANCELLED'",
            nativeQuery = true)
    Booking bookingAtTime(long slotId, long therapistId, LocalDate date);

    @Query(
            value = "select count(*) from booking b\n" + "inner join slot s\n"
                    + "on b.slot_id = s.slotid\n"
                    + "inner join specific_stylist_schedule ssch\n"
                    + "on b.stylist_schedule_id = ssch.stylist_schedule_id\n"
                    + "inner join stylist_schedule ss\n"
                    + "on b.stylist_schedule_id = ss.stylist_schedule_id\n"
                    + "inner join shift sh\n"
                    + "on ssch.shift_id = sh.shift_id\n"
                    + "where s.slottime >= sh.start_time and s.slottime < sh.end_time and  sh.shift_id = ?1 and b.status = 'COMPLETED' \n"
                    + "and ss.account_id = ?2 and ss.working_day = ?3;",
            nativeQuery = true)
    int countTotalBookingCompleteInShift(long shiftId, long accountId, LocalDate date);

    @Query(
            value = "SELECT b.* FROM booking b\n" + "INNER JOIN slot s ON b.slot_id = s.slotid\n"
                    + "INNER JOIN therapist_schedule ts ON b.therapist_schedule_id = ts.therapist_schedule_id\n"
                    + "WHERE s.slotid = ?1 AND b.booking_day = ?2 AND ts.therapist_schedule_id = ?3 \n"
                    + "AND b.status != 'CANCELLED'",
            nativeQuery = true)
    Booking getBySlotSlotidAndBookingDayAndTherapistScheduleTherapistScheduleId(
            long slotId, LocalDate date, long therapistScheduleId);
}
