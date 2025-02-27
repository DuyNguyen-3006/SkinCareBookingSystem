package com.skincare_booking_system.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TherapistUpdate {
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Boolean status;
    Integer yearExperience;
}
