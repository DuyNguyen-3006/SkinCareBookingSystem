package com.skincare_booking_system.entity;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Therapist {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;
    String firstName;
    String lastName;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Boolean status;
    Integer yearExperience;
    @ManyToMany
    Set<Role> roles;


}
