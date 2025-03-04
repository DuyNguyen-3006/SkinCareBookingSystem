package com.skincare_booking_system.repository;

import com.skincare_booking_system.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findBookingByBookingId(long id);
}
