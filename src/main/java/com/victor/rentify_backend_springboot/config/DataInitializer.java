package com.victor.rentify_backend_springboot.config;

import com.victor.rentify_backend_springboot.model.Role;
import com.victor.rentify_backend_springboot.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            String[][] roles = {
                {"RENTER", "Renter role"},
                {"HOST", "Host role"},
                {"ADMIN", "Admin role"}
            };
            for (String[] roleData : roles) {
                roleRepository.findByName(roleData[0])
                    .orElseGet(() -> roleRepository.save(new Role(roleData[0], roleData[1])));
            }
        };
    }
}
