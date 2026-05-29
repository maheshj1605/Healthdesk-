package com.healthdesk.controller;

import com.healthdesk.dto.EMRRequest;
import com.healthdesk.model.EMR;
import com.healthdesk.service.*;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/emr")
public class EMRController {

    private final EMRService emrService;
    private final PrescriptionPdfService pdfService;

    public EMRController(EMRService emrService, PrescriptionPdfService pdfService) {
        this.emrService = emrService;
        this.pdfService = pdfService;
    }

    @PostMapping
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<EMR> create(@Valid @RequestBody EMRRequest req, Authentication auth) {
        return ResponseEntity.ok(emrService.createRecord(auth.getName(), req));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST')")
    public ResponseEntity<List<EMR>> getByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(emrService.getPatientRecords(patientId));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<EMR>> myRecords(Authentication auth) {
        // Patients fetch their own records via a separate endpoint
        return ResponseEntity.ok(emrService.getMyRecords(auth.getName()));
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','PATIENT')")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id, Authentication auth) {
        EMR emr = emrService.getById(id);
        // Patients can only download their own records
        if (auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"))) {
            if (!emr.getPatient().getEmail().equals(auth.getName())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        byte[] pdf = pdfService.generatePrescriptionPdf(emr);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=prescription_" + id + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .body(pdf);
    }
}
