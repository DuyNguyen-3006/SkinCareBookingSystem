package com.skincare_booking_system.dto.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffCreateBookingRequest {
    String phoneNumber;
    long slotId;
    LocalDate bookingDate;
    Set<Long> serviceId;
    long therapistId;
    long voucherId;
}

