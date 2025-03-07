package com.skincare_booking_system.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageResponse {

    String packageName;
    Double packageFinalPrice;
    Boolean packageActive;
    LocalTime duration;
    List<ServicesResponse> services;
}
