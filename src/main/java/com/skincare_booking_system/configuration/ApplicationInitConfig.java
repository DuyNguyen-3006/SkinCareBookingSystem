package com.skincare_booking_system.configuration;

import java.util.HashSet;
import java.util.Set;

import com.skincare_booking_system.entity.Role;
import com.skincare_booking_system.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.skincare_booking_system.constant.Roles;
import com.skincare_booking_system.entity.User;
import com.skincare_booking_system.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
// Class nay dung de tao account admin moi lan start application len
public class ApplicationInitConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;
    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("ADMIN").isEmpty()) {
                Role adminRole = roleRepository.findById(Roles.ADMIN.name())
                        .orElseGet(() -> {
                            Role newRole = new Role();
                            newRole.setName(Roles.ADMIN.name());
                            return roleRepository.save(newRole);
                        });

                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(roles)
                        .build();

                userRepository.save(user);
                log.warn("Admin user created with default password: admin, please change password ");
            }
        };
    }

}
