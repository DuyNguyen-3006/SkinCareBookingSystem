package com.skincare_booking_system.dto.request;

import com.skincare_booking_system.validator.ImageURL;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

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
    @NotBlank(message = "IMG_URL_REQUIRED")
    @Size(max = 500, message = "IMG_URL_TOO_LONG")
    @ImageURL(message = "IMG_URL_INVALID")
    String imgUrl;
}
