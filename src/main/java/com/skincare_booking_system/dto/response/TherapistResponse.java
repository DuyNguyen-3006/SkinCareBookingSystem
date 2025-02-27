package com.skincare_booking_system.dto.response;

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
