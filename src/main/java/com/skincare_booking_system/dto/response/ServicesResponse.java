package com.skincare_booking_system.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServicesResponse {

    String serviceName;
    String description;
    Double price;
    Boolean isActive;
    LocalTime duration;
    String imgUrl;
}
