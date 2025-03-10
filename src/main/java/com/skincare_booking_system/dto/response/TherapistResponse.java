package com.skincare_booking_system.dto.response;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.ManyToMany;

import com.skincare_booking_system.entities.Role;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TherapistResponse {
    String id;
    String username;
    String fullName;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Boolean status;
    Integer yearExperience;

    @ManyToMany
    Set<Role> roles;
}
