package com.skincare_booking_system.mapper;

import org.mapstruct.Mapper;

import com.skincare_booking_system.dto.response.BookingResponse;
import com.skincare_booking_system.entities.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toBookingResponse(Booking booking);
}
