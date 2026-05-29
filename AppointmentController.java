package com.healthdesk.controller;

import com.healthdesk.dto.AppointmentRequest;
import com.healthdesk.model.*;
import com.healthdesk.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Appointment> book(@Valid @RequestBody AppointmentRequest req,
                                             Authentication auth) {
        return ResponseEntity.ok(appointmentService.book(auth.getName(), req));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<Appointment>> mine(Authentication auth) {
        return ResponseEntity.ok(appointmentService.getMyAppointments(auth.getName()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST')")
    public ResponseEntity<Appointment> updateStatus(@PathVariable Long id,
                                                     @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<List<Appointment>> all() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }
}
