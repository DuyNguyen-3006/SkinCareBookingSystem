package com.skincare_booking_system.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VoucherResponse {

    String voucherName;
    String voucherCode;
    Double percentDiscount;
    Boolean isActive;
    LocalDate expiryDate;
    Integer quantity;
}
