package com.skincare_booking_system.dto.request;

import com.skincare_booking_system.entity.Role;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TherapistRequest {
    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Integer yearExperience;
}
