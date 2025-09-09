package com.victor.rentify_backend_springboot.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/renter")
    @PreAuthorize("hasRole('RENTER')")
    public String renterAccess() {
        return "Renter content";
    }

    @GetMapping("/host")
    @PreAuthorize("hasRole('HOST')")
    public String hostAccess() {
        return "Host content";
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin content";
    }
}
