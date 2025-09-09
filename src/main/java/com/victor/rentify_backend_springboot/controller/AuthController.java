package com.victor.rentify_backend_springboot.controller;

import com.victor.rentify_backend_springboot.model.Role;
import com.victor.rentify_backend_springboot.model.User;
import com.victor.rentify_backend_springboot.repository.RoleRepository;
import com.victor.rentify_backend_springboot.repository.UserRepository;
import com.victor.rentify_backend_springboot.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.victor.rentify_backend_springboot.service.FileStorageService;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<?> register(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("displayname") String displayname,
            @RequestParam("email") String email,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) {
        if (userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
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
        Role role = roleRepository.findByName("RENTER").orElseThrow();
        user.setRoles(new HashSet<>(Collections.singletonList(role)));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping(value = "/login", consumes = {"multipart/form-data"})
    public ResponseEntity<?> login(
        @RequestParam("username") String username,
        @RequestParam("password") String password) {
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(username, password));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    String token = jwtUtil.generateToken(username);

    // Fetch user and roles
    User user = userRepository.findByUsername(username).orElse(null);
    if (user == null) {
        return ResponseEntity.status(404).body("User not found");
    }
    // Extract role names as a list
    java.util.List<String> roles = user.getRoles().stream()
        .map(Role::getName)
        .toList();

    return ResponseEntity.ok(java.util.Map.of(
        "token", token,
        "roles", roles,
        "username", user.getUsername(),
        "displayname", user.getDisplayname(),
        "email", user.getEmail(),
        "imageUrl", user.getImageUrl()
    ));
    }
}
