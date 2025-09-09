package com.victor.rentify_backend_springboot.dto;

import java.util.List;
import com.victor.rentify_backend_springboot.dto.ImageDTO;

public class PropertyDTO {
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
    private String hostDisplayname;
    private List<ImageDTO> images;
    private List<String> amenities;

    // Getters and setters
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
    public String getHostDisplayname() { return hostDisplayname; }
    public void setHostDisplayname(String hostDisplayname) { this.hostDisplayname = hostDisplayname; }
    public List<ImageDTO> getImages() { return images; }
    public void setImages(List<ImageDTO> images) { this.images = images; }
    public List<String> getAmenities() { return amenities; }
    public void setAmenities(List<String> amenities) { this.amenities = amenities; }
}
