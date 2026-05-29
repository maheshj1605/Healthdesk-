package com.healthdesk.controller;

import com.healthdesk.model.*;
import com.healthdesk.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository userRepo;

    public UserController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors() {
        // Password is @JsonIgnore on User model — safe to return directly
        return ResponseEntity.ok(userRepo.findByRole(Role.DOCTOR));
    }

    @GetMapping("/admin/patients")
    public ResponseEntity<List<User>> getPatients() {
        return ResponseEntity.ok(userRepo.findByRole(Role.PATIENT));
    }
}
