package com.skincare_booking_system.entities;

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
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String username;
    String password;
    String fullname;
    String email;
    String phone;
    String address;
    String gender;
    LocalDate birthDate;
    Boolean status;
    @ManyToMany
    Set<Role> roles;
}
