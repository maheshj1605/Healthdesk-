package com.healthdesk.service;

import com.healthdesk.dto.EMRRequest;
import com.healthdesk.model.*;
import com.healthdesk.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class EMRService {

    private final EMRRepository emrRepo;
    private final UserRepository userRepo;

    public EMRService(EMRRepository emrRepo, UserRepository userRepo) {
        this.emrRepo = emrRepo;
        this.userRepo = userRepo;
    }

    public EMR createRecord(String doctorEmail, EMRRequest req) {
        User doctor = userRepo.findByEmail(doctorEmail)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));
        User patient = userRepo.findById(req.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient not found"));

        EMR emr = EMR.builder()
            .doctor(doctor)
            .patient(patient)
            .visitDate(LocalDate.now())
            .diagnosis(req.getDiagnosis())
            .prescription(req.getPrescription())
            .notes(req.getNotes())
            .build();
        return emrRepo.save(emr);
    }

    public List<EMR> getPatientRecords(Long patientId) {
        return emrRepo.findByPatientIdOrderByVisitDateDesc(patientId);
    }

    /** Used by patients to fetch their own records */
    public List<EMR> getMyRecords(String email) {
        User patient = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return emrRepo.findByPatientIdOrderByVisitDateDesc(patient.getId());
    }

    public EMR getById(Long id) {
        return emrRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("EMR record not found"));
    }
}
