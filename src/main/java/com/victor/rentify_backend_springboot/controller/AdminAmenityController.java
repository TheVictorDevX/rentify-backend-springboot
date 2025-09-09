package com.victor.rentify_backend_springboot.controller;

import com.victor.rentify_backend_springboot.model.Amenity;
import com.victor.rentify_backend_springboot.repository.AmenityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/amenities")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAmenityController {
    @Autowired
    private AmenityRepository amenityRepository;

    @GetMapping
    public ResponseEntity<?> getAllAmenities() {
        return ResponseEntity.ok(amenityRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAmenityById(@PathVariable Long id) {
        return amenityRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAmenity(@RequestParam String amenityName) {
        if (amenityRepository.findByAmenityName(amenityName) != null) {
            return ResponseEntity.badRequest().body("Amenity already exists");
        }
        Amenity amenity = new Amenity();
        amenity.setAmenityName(amenityName);
        amenityRepository.save(amenity);
        return ResponseEntity.ok("Amenity created successfully");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAmenity(@PathVariable Long id, @RequestParam String amenityName) {
        return amenityRepository.findById(id).map(amenity -> {
            amenity.setAmenityName(amenityName);
            amenityRepository.save(amenity);
            return ResponseEntity.ok("Amenity updated successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAmenity(@PathVariable Long id) {
        if (!amenityRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        amenityRepository.deleteById(id);
        return ResponseEntity.ok("Amenity deleted successfully");
    }
}
