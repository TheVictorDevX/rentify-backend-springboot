package com.victor.rentify_backend_springboot.controller;

import com.victor.rentify_backend_springboot.model.*;
import com.victor.rentify_backend_springboot.repository.*;
import com.victor.rentify_backend_springboot.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private AmenityRepository amenityRepository;
    @Autowired
    private PropertyAmenityRepository propertyAmenityRepository;
    @Autowired
    private PropertyImageRepository propertyImageRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FileStorageService fileStorageService;

    @PreAuthorize("hasRole('HOST')")
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> createProperty(
            @RequestParam String propertyName,
            @RequestParam String propertyType,
            @RequestParam String streetAddress,
            @RequestParam String city,
            @RequestParam String state,
            @RequestParam String zipCode,
            @RequestParam String description,
            @RequestParam int numberOfBedrooms,
            @RequestParam double price,
            @RequestParam String pricePer,
            @RequestParam List<Long> amenityIds, // amenity IDs
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication
    ) {
        // Get the authenticated user (host)
        String username = authentication.getName();
        User host = userRepository.findByUsername(username).orElseThrow();

        Property property = new Property();
        property.setPropertyName(propertyName);
        property.setPropertyType(propertyType);
        property.setStreetAddress(streetAddress);
        property.setCity(city);
        property.setState(state);
        property.setZipCode(zipCode);
        property.setDescription(description);
        property.setNumberOfBedrooms(numberOfBedrooms);
        property.setPrice(price);
        property.setPricePer(pricePer);
        property.setHost(host);
        property = propertyRepository.save(property);

        // Handle amenities by ID only; do not create new amenities here
        for (Long amenityId : amenityIds) {
            if (amenityId == null) continue;
            Optional<Amenity> amenityOpt = amenityRepository.findById(amenityId);
            if (amenityOpt.isPresent()) {
                Amenity amenity = amenityOpt.get();
                PropertyAmenity pa = new PropertyAmenity();
                pa.setProperty(property);
                pa.setAmenity(amenity);
                propertyAmenityRepository.save(pa);
            }
            // else: ignore unknown/invalid amenity IDs
        }

        // Handle images
        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String imageUrl = fileStorageService.saveFile(image);
                        PropertyImage pi = new PropertyImage();
                        pi.setProperty(property);
                        pi.setImageUrl(imageUrl);
                        propertyImageRepository.save(pi);
                    } catch (Exception e) {
                        return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.ok("Property listed successfully");
    }
    @PreAuthorize("hasRole('HOST')")
    @GetMapping
    public ResponseEntity<?> getMyProperties(Authentication authentication) {
        String username = authentication.getName();
        User host = userRepository.findByUsername(username).orElseThrow();
        List<Property> properties = propertyRepository.findAll().stream()
                .filter(p -> p.getHost() != null && p.getHost().getId().equals(host.getId()))
                .toList();

        List<com.victor.rentify_backend_springboot.dto.PropertyDTO> propertyDTOs = properties.stream().map(property -> {
            com.victor.rentify_backend_springboot.dto.PropertyDTO dto = new com.victor.rentify_backend_springboot.dto.PropertyDTO();
            dto.setPropertyId(property.getPropertyId());
            dto.setPropertyName(property.getPropertyName());
            dto.setPropertyType(property.getPropertyType());
            dto.setStreetAddress(property.getStreetAddress());
            dto.setCity(property.getCity());
            dto.setState(property.getState());
            dto.setZipCode(property.getZipCode());
            dto.setDescription(property.getDescription());
            dto.setNumberOfBedrooms(property.getNumberOfBedrooms());
            dto.setPrice(property.getPrice());
            dto.setPricePer(property.getPricePer());
            dto.setHostDisplayname(property.getHost() != null ? property.getHost().getDisplayname() : null);
            // Map images as objects with url property
            List<com.victor.rentify_backend_springboot.dto.ImageDTO> images = property.getImages().stream()
                .map(img -> new com.victor.rentify_backend_springboot.dto.ImageDTO(img.getImageUrl()))
                .toList();
            dto.setImages(images);
            // Map amenities
            List<String> amenities = property.getPropertyAmenities().stream()
                .map(pa -> pa.getAmenity() != null ? pa.getAmenity().getAmenityName() : null)
                .filter(a -> a != null)
                .toList();
            dto.setAmenities(amenities);
            return dto;
        }).toList();
        return ResponseEntity.ok(propertyDTOs);
    }

    @PreAuthorize("hasRole('HOST')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getMyProperty(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User host = userRepository.findByUsername(username).orElseThrow();
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty() || !propertyOpt.get().getHost().getId().equals(host.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        return ResponseEntity.ok(propertyOpt.get());
    }

    @PreAuthorize("hasRole('HOST')")
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateMyProperty(
            @PathVariable Long id,
            @RequestParam(required = false) String propertyName,
            @RequestParam(required = false) String propertyType,
            @RequestParam(required = false) String streetAddress,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String zipCode,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer numberOfBedrooms,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String pricePer,
            @RequestParam(required = false) List<String> amenities,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            Authentication authentication) {
        String username = authentication.getName();
        User host = userRepository.findByUsername(username).orElseThrow();
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty() || !propertyOpt.get().getHost().getId().equals(host.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        Property property = propertyOpt.get();
        if (propertyName != null) property.setPropertyName(propertyName);
        if (propertyType != null) property.setPropertyType(propertyType);
        if (streetAddress != null) property.setStreetAddress(streetAddress);
        if (city != null) property.setCity(city);
        if (state != null) property.setState(state);
        if (zipCode != null) property.setZipCode(zipCode);
        if (description != null) property.setDescription(description);
        if (numberOfBedrooms != null) property.setNumberOfBedrooms(numberOfBedrooms);
        if (price != null) property.setPrice(price);
        if (pricePer != null) property.setPricePer(pricePer);
        propertyRepository.save(property);
        // Update amenities (replace all if provided)
        if (amenities != null) {
            propertyAmenityRepository.deleteAll(property.getPropertyAmenities());
            for (String amenityName : amenities) {
                Amenity amenity = amenityRepository.findByAmenityName(amenityName);
                if (amenity == null) {
                    amenity = new Amenity();
                    amenity.setAmenityName(amenityName);
                    amenity = amenityRepository.save(amenity);
                }
                PropertyAmenity pa = new PropertyAmenity();
                pa.setProperty(property);
                pa.setAmenity(amenity);
                propertyAmenityRepository.save(pa);
            }
        }
        // Replace all images if new images are provided
        if (images != null) {
            // Delete all old images for this property
            propertyImageRepository.deleteAll(property.getImages());
            property.getImages().clear();
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    try {
                        String imageUrl = fileStorageService.saveFile(image);
                        PropertyImage pi = new PropertyImage();
                        pi.setProperty(property);
                        pi.setImageUrl(imageUrl);
                        propertyImageRepository.save(pi);
                    } catch (Exception e) {
                        // skip failed image
                    }
                }
            }
        }
        return ResponseEntity.ok("Property updated successfully");
    }

    @PreAuthorize("hasRole('HOST')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMyProperty(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        User host = userRepository.findByUsername(username).orElseThrow();
        Optional<Property> propertyOpt = propertyRepository.findById(id);
        if (propertyOpt.isEmpty() || !propertyOpt.get().getHost().getId().equals(host.getId())) {
            return ResponseEntity.status(403).body("Forbidden");
        }
        propertyRepository.deleteById(id);
        return ResponseEntity.ok("Property deleted successfully");
    }

    // Public endpoint for guests to fetch all property listings
    @GetMapping("/public")
    public ResponseEntity<?> getAllPropertiesForGuests() {
        List<Property> properties = propertyRepository.findAll();
        List<com.victor.rentify_backend_springboot.dto.PropertyDTO> propertyDTOs = properties.stream().map(property -> {
            com.victor.rentify_backend_springboot.dto.PropertyDTO dto = new com.victor.rentify_backend_springboot.dto.PropertyDTO();
            dto.setPropertyId(property.getPropertyId());
            dto.setPropertyName(property.getPropertyName());
            dto.setPropertyType(property.getPropertyType());
            dto.setStreetAddress(property.getStreetAddress());
            dto.setCity(property.getCity());
            dto.setState(property.getState());
            dto.setZipCode(property.getZipCode());
            dto.setDescription(property.getDescription());
            dto.setNumberOfBedrooms(property.getNumberOfBedrooms());
            dto.setPrice(property.getPrice());
            dto.setPricePer(property.getPricePer());
            dto.setHostDisplayname(property.getHost() != null ? property.getHost().getDisplayname() : null);
            // Map images as objects with url property
            List<com.victor.rentify_backend_springboot.dto.ImageDTO> images = property.getImages().stream()
                .map(img -> new com.victor.rentify_backend_springboot.dto.ImageDTO(img.getImageUrl()))
                .toList();
            dto.setImages(images);
            // Map amenities
            List<String> amenities = property.getPropertyAmenities().stream()
                .map(pa -> pa.getAmenity() != null ? pa.getAmenity().getAmenityName() : null)
                .filter(a -> a != null)
                .toList();
            dto.setAmenities(amenities);
            return dto;
        }).toList();
        return ResponseEntity.ok(propertyDTOs);
    }
}
