package com.skincare_booking_system.dto.request;

import com.skincare_booking_system.validator.GenderConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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
    @GenderConstraint( message = "GENDER_INVALID")
    String gender;
    LocalDate birthDate;
    List<String> roles;

}
