package com.skincare_booking_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

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
    String gender;
    LocalDate birthDate;

}
