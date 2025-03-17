package com.skincare_booking_system.dto.request;

import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ServicesUpdateRequest {

    String serviceName;

    @Size(min = 3, max = 150, message = "DESCRIPTION_INVALID")
    String description;

    @Min(value = 0, message = "PRICE_INVALID")
    Double price;

    @NotNull(message = "DURATION_REQUIRED")
    LocalTime duration;

    MultipartFile imgUrl;
}
