package com.victor.rentify_backend_springboot.controller;

import com.victor.rentify_backend_springboot.model.Role;
import com.victor.rentify_backend_springboot.model.User;
import com.victor.rentify_backend_springboot.repository.RoleRepository;
import com.victor.rentify_backend_springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.victor.rentify_backend_springboot.service.FileStorageService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FileStorageService fileStorageService;

    // List all users
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update user (with optional image upload and roles)
    @PutMapping(value = "/users/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String displayname,
            @RequestParam(required = false) String email,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam(required = false) List<String> roles) {
        return userRepository.findById(id).map(user -> {
            if (username != null) user.setUsername(username);
            if (password != null) user.setPassword(passwordEncoder.encode(password));
            if (displayname != null) user.setDisplayname(displayname);
            if (email != null) user.setEmail(email);
            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = fileStorageService.saveFile(imageFile);
                    user.setImageUrl(imageUrl);
                } catch (Exception e) {
                    return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
                }
            }
            if (roles != null && !roles.isEmpty()) {
                Set<Role> userRoles = new HashSet<>();
                for (String roleName : roles) {
                    Role role = roleRepository.findByName(roleName.toUpperCase()).orElse(null);
                    if (role != null) userRoles.add(role);
                }
                if (!userRoles.isEmpty()) user.setRoles(userRoles);
            }
            userRepository.save(user);
            return ResponseEntity.ok("User updated successfully");
        }).orElse(ResponseEntity.notFound().build());
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully");
    }

    @PostMapping(value = "/users", consumes = {"multipart/form-data"})
    public ResponseEntity<?> createUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String displayname,
            @RequestParam String email,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @RequestParam List<String> roles) {
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        Set<Role> userRoles = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName.toUpperCase()).orElse(null);
            if (role != null) {
                userRoles.add(role);
            }
        }
        if (userRoles.isEmpty()) {
            return ResponseEntity.badRequest().body("No valid roles provided");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setDisplayname(displayname);
        user.setEmail(email);
        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                imageUrl = fileStorageService.saveFile(imageFile);
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Image upload failed: " + e.getMessage());
            }
        }
        user.setImageUrl(imageUrl);
        user.setRoles(userRoles);
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }
}
