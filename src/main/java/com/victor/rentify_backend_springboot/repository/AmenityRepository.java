package com.victor.rentify_backend_springboot.repository;

import com.victor.rentify_backend_springboot.model.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {
    Amenity findByAmenityName(String amenityName);
}
