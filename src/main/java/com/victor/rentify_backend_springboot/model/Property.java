package com.victor.rentify_backend_springboot.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "properties")
public class Property {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long propertyId;

    private String propertyName;
    private String propertyType;
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private String description;
    private int numberOfBedrooms;
    private double price;
    private String pricePer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id")
    private User host;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyImage> images = new HashSet<>();

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PropertyAmenity> propertyAmenities = new HashSet<>();

    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
    public String getPropertyName() { return propertyName; }
    public void setPropertyName(String propertyName) { this.propertyName = propertyName; }
    public String getPropertyType() { return propertyType; }
    public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
    public String getStreetAddress() { return streetAddress; }
    public void setStreetAddress(String streetAddress) { this.streetAddress = streetAddress; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getNumberOfBedrooms() { return numberOfBedrooms; }
    public void setNumberOfBedrooms(int numberOfBedrooms) { this.numberOfBedrooms = numberOfBedrooms; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getPricePer() { return pricePer; }
    public void setPricePer(String pricePer) { this.pricePer = pricePer; }
    public User getHost() { return host; }
    public void setHost(User host) { this.host = host; }
    public Set<PropertyImage> getImages() { return images; }
    public void setImages(Set<PropertyImage> images) { this.images = images; }
    public Set<PropertyAmenity> getPropertyAmenities() { return propertyAmenities; }
    public void setPropertyAmenities(Set<PropertyAmenity> propertyAmenities) { this.propertyAmenities = propertyAmenities; }
}
