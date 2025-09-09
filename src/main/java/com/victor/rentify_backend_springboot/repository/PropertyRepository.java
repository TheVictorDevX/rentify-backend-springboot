package com.victor.rentify_backend_springboot.repository;

import com.victor.rentify_backend_springboot.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyRepository extends JpaRepository<Property, Long> {
}
