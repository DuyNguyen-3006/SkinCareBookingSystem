package com.skincare_booking_system.dto.response;


import com.skincare_booking_system.entities.Services;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PackageResponse {
    String packageId;
    String packageName;
    Double packageFinalPrice;
    Boolean packageActive;
    List<String> servicesName;
    List<Services> services;
}
