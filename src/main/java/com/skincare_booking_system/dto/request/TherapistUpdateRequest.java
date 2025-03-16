package com.skincare_booking_system.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Pattern;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TherapistUpdateRequest {
    String fullName;
    String email;

    @Pattern(regexp = "^(84|0[35789])\\d{8}$", message = "PHONENUMBER_INVALID")
    String phone;
    MultipartFile imgUrl;

    String address;
    String gender;
    LocalDate birthDate;
    Integer yearExperience;
}
