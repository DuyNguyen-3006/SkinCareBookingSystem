package com.skincare_booking_system.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Services {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String serviceId;
    String serviceName;
    String description;
    Double price;
    String category;
    Boolean isActive;

    @JsonIgnore
    @ManyToMany(mappedBy = "services")
    List<Package> packages;

}
