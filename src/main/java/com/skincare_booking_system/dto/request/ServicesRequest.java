package com.skincare_booking_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
    @Size(min = 3, max = 150, message = "DESCRIPTION_INVALID")
    String description;
    @Min(value = 0, message = "PRICE_INVALID")
    Double price;
    @Size(min = 3, max = 50, message = "CATEGORY_INVALID")
    String category;
    Boolean isActive;
}
