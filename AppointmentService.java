package com.healthdesk.service;

import com.healthdesk.dto.AppointmentRequest;
import com.healthdesk.model.*;
import com.healthdesk.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final UserRepository userRepo;

    public AppointmentService(AppointmentRepository appointmentRepo, UserRepository userRepo) {
        this.appointmentRepo = appointmentRepo;
        this.userRepo = userRepo;
    }

    public Appointment book(String patientEmail, AppointmentRequest req) {
        User patient = userRepo.findByEmail(patientEmail)
            .orElseThrow(() -> new RuntimeException("Patient not found"));
        User doctor = userRepo.findById(req.getDoctorId())
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Double-booking check
        if (appointmentRepo.isDoctorBooked(doctor.getId(), req.getAppointmentTime())) {
            throw new IllegalArgumentException(
                "Dr. " + doctor.getName() + " is already booked at that time. Please choose another slot.");
        }

        Appointment appt = Appointment.builder()
            .patient(patient)
            .doctor(doctor)
            .appointmentTime(req.getAppointmentTime())
            .status(AppointmentStatus.SCHEDULED)
            .notes(req.getNotes())
            .build();
        return appointmentRepo.save(appt);
    }

    public List<Appointment> getMyAppointments(String email) {
        User user = userRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.DOCTOR) {
            return appointmentRepo.findByDoctorIdOrderByAppointmentTimeDesc(user.getId());
        }
        return appointmentRepo.findByPatientIdOrderByAppointmentTimeDesc(user.getId());
    }

    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment appt = appointmentRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Appointment not found"));
        appt.setStatus(status);
        return appointmentRepo.save(appt);
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepo.findAll();
    }
}
