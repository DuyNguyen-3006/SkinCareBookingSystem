package com.skincare_booking_system.dto.response;

import java.time.LocalTime;
import java.util.Set;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServicesResponse {
    Long serviceId;
    String serviceName;
    String description;
    Double price;
    Boolean isActive;
    LocalTime duration;
    String imgUrl;
}
