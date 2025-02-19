package com.skincare_booking_system.dto.request;

import jakarta.validation.constraints.Min;
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
public class ServicesRequest {
    String serviceName;
    String description;
    @Min(value = 0, message = "PRICE_INVALID")
    Double price;
    String category;
    Boolean isActive;
}
