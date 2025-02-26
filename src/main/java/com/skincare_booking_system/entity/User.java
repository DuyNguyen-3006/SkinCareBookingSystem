package com.skincare_booking_system.entity;

import java.time.LocalDate;
import java.util.Set;

import jakarta.persistence.*;

import com.skincare_booking_system.constant.Gender;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
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
    Gender gender;
    LocalDate birthDate;

    @ManyToMany
    Set<Role> roles;
}
