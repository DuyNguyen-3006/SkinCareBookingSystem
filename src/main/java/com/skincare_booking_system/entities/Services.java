package com.skincare_booking_system.entities;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long serviceId;

    String serviceName;
    String description;

    @Column(nullable = false)
    int price;

    Boolean isActive;

    LocalTime duration;

    String imgUrl;


    @ManyToMany(mappedBy = "services")
    @JsonIgnore
    Set<Booking> bookings;


}
