package com.skincare_booking_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserRegisterRequest {

    @Size(min = 3, max = 20, message = "USERNAME_INVALID")
    String username;

    @Size(min = 8, max = 20, message = "PASSWORD_INVALID")
    String password;
    @NotBlank(message = "BLANK_FIELD")
    String firstName;
    @NotBlank(message = "BLANK_FIELD")
    String lastName;
    @Email
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;

}
