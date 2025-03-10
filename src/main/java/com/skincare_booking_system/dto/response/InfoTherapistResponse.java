package com.skincare_booking_system.dto.response;

import java.time.LocalDate;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InfoTherapistResponse {
    String username;
    String fullName;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Integer yearExperience;
}
