package com.victor.rentify_backend_springboot.repository;

import com.victor.rentify_backend_springboot.model.PropertyImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PropertyImageRepository extends JpaRepository<PropertyImage, Long> {
}
