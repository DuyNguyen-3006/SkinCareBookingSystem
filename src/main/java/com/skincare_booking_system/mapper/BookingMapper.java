package com.skincare_booking_system.mapper;

import com.skincare_booking_system.dto.response.BookingResponse;
import com.skincare_booking_system.entities.Booking;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingResponse toBookingResponse(Booking booking);
}
