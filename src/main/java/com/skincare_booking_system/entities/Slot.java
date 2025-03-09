package com.skincare_booking_system.entities;

import java.time.LocalTime;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Slot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long slotid;

    LocalTime slottime;

    boolean deleted = false;

        @OneToMany(mappedBy = "slot")
        @JsonIgnore
        Set<Booking> bookings;
}
