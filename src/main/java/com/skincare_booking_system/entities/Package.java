package com.skincare_booking_system.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Package {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String packageId;
    String packageName;
    Double packageFinalPrice;
    Boolean packageActive;
    @ManyToMany
    @JoinTable(
            name = "package_services",
            joinColumns = @JoinColumn(name = "package_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    List<Services> services;
    List<String>servicesNames;

}
