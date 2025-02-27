package com.skincare_booking_system.dto.request;

import com.skincare_booking_system.entity.Role;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Pattern;
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
    String fullName;
    String email;
    @Pattern(regexp = "^(84|0[35789])\\d{8}$", message = "PHONENUMBER_INVALID")
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Integer yearExperience;
}
