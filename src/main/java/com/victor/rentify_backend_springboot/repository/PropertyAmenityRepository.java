package com.victor.rentify_backend_springboot.repository;

import com.victor.rentify_backend_springboot.model.PropertyAmenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyAmenityRepository extends JpaRepository<PropertyAmenity, Long> {
}
