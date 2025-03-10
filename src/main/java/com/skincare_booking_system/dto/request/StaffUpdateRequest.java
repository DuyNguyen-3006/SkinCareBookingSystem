package com.skincare_booking_system.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import com.skincare_booking_system.validator.GenderConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StaffUpdateRequest {
    @NotBlank(message = "BLANK_FIELD")
    String firstName;

    @NotBlank(message = "BLANK_FIELD")
    String lastName;

    @Email(message = "EMAIL_INVALID")
    String email;

    @Pattern(regexp = "^(84|0[35789])\\d{8}$", message = "PHONENUMBER_INVALID")
    String phone;

    String address;

    @GenderConstraint(message = "GENDER_INVALID")
    String gender;

    LocalDate birthDate;
    Boolean status;
}
