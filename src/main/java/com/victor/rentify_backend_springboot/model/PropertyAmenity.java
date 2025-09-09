package com.victor.rentify_backend_springboot.model;

import jakarta.persistence.*;

@Entity
@Table(name = "property_amenities")
public class PropertyAmenity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyAmenityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "amenity_id")
    private Amenity amenity;

    public Long getPropertyAmenityId() { return propertyAmenityId; }
    public void setPropertyAmenityId(Long propertyAmenityId) { this.propertyAmenityId = propertyAmenityId; }
    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }
    public Amenity getAmenity() { return amenity; }
    public void setAmenity(Amenity amenity) { this.amenity = amenity; }
}
