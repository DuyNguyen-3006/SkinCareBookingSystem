package com.skincare_booking_system.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class ReminderBooking {
    String to;
    String subject;
    LocalDate date;
    LocalTime time;
    String therapistName;
}
