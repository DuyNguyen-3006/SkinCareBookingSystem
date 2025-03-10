package com.skincare_booking_system.entities;

import java.time.LocalDate;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Therapist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String username;
    String password;
    String fullName;
    String email;
    String phone;
    String address;
    String gender;
    String image;
    LocalDate birthDate;
    Boolean status;
    Integer yearExperience;

    @OneToMany(mappedBy = "therapist")
    @JsonIgnore
    Set<TherapistSchedule> therapistSchedules;

    @OneToMany(mappedBy = "therapist")
    @JsonIgnore
    Set<Booking> bookings;

    @ManyToMany
    Set<Role> roles;
}
