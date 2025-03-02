package com.skincare_booking_system.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
