package com.victor.rentify_backend_springboot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "amenities")
public class Amenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long amenityId;

    private String amenityName;

    @OneToMany(mappedBy = "amenity", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<PropertyAmenity> propertyAmenities = new HashSet<>();

    public Long getAmenityId() { return amenityId; }
    public void setAmenityId(Long amenityId) { this.amenityId = amenityId; }
    public String getAmenityName() { return amenityName; }
    public void setAmenityName(String amenityName) { this.amenityName = amenityName; }
    public Set<PropertyAmenity> getPropertyAmenities() { return propertyAmenities; }
    public void setPropertyAmenities(Set<PropertyAmenity> propertyAmenities) { this.propertyAmenities = propertyAmenities; }
}
