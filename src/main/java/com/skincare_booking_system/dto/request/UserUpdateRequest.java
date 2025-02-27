package com.skincare_booking_system.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import com.skincare_booking_system.validator.GenderConstraint;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

    @NotBlank(message = "BLANK_FIELD")
    String firstName;

    @NotBlank(message = "BLANK_FIELD")
    String lastName;

    @Email(message = "EMAIL_INVALID")
    String email;

    String phone;

    @NotBlank(message = "BLANK_FIELD")
    String address;

    @GenderConstraint(message = "GENDER_INVALID")
    String gender;

    LocalDate birthDate;
}
