package com.skincare_booking_system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String serviceId;
    String serviceName;
    String description;
    Double price;
    Boolean isActive;
    LocalTime duration;
    String imgUrl;

    @JsonIgnore
    @ManyToMany(mappedBy = "services")
    List<Package> packages;

    @ManyToMany(mappedBy = "services")
    @JsonIgnore
    Set<Booking> bookings;
}
