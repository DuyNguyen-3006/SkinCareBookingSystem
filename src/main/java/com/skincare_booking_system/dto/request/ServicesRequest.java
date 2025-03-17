package com.skincare_booking_system.dto.request;

import java.time.LocalTime;
import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.skincare_booking_system.validator.ImageURL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ServicesRequest {
    String serviceName;

    @Size(min = 3, max = 150, message = "DESCRIPTION_REQUIRED")
    String description;

    @Min(value = 0, message = "PRICE_INVALID")
    Double price;

    Boolean isActive;

    @NotNull(message = "DURATION_REQUIRED")
    LocalTime duration;
    MultipartFile imgUrl;
}
