package com.victor.rentify_backend_springboot.dto;

public class ImageDTO {
    private String url;

    public ImageDTO(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
