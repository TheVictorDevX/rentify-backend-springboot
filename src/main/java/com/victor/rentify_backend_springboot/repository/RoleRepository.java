package com.victor.rentify_backend_springboot.repository;

import com.victor.rentify_backend_springboot.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
