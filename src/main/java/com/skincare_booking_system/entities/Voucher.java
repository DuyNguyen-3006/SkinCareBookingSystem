package com.skincare_booking_system.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String voucherId;

    @Column(unique = true)
    String voucherName;

    @Column(unique = true)
    String voucherCode;

    Double percentDiscount;

    Boolean isActive;

    LocalDate expiryDate;

    Integer quantity;
    @OneToMany(mappedBy = "voucher")
    @JsonIgnore
    List<Booking> bookings;
}
