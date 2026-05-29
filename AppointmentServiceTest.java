package com.healthdesk.service;

import com.healthdesk.dto.AppointmentRequest;
import com.healthdesk.model.*;
import com.healthdesk.repository.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository appointmentRepo;
    @Mock UserRepository userRepo;

    @InjectMocks AppointmentService appointmentService;

    private User patient;
    private User doctor;

    @BeforeEach
    void setUp() {
        patient = User.builder().id(1L).email("p@test.com").role(Role.PATIENT).build();
        doctor  = User.builder().id(2L).name("Smith").email("d@test.com").role(Role.DOCTOR).build();
    }

    @Test
    void book_success() {
        AppointmentRequest req = new AppointmentRequest();
        req.setDoctorId(2L);
        req.setAppointmentTime(LocalDateTime.now().plusDays(1));

        when(userRepo.findByEmail("p@test.com")).thenReturn(Optional.of(patient));
        when(userRepo.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepo.isDoctorBooked(2L, req.getAppointmentTime())).thenReturn(false);

        Appointment saved = Appointment.builder()
            .id(1L).patient(patient).doctor(doctor)
            .appointmentTime(req.getAppointmentTime())
            .status(AppointmentStatus.SCHEDULED).build();
        when(appointmentRepo.save(any())).thenReturn(saved);

        Appointment result = appointmentService.book("p@test.com", req);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.SCHEDULED);
        assertThat(result.getDoctor().getId()).isEqualTo(2L);
    }

    @Test
    void book_doctorAlreadyBooked_throws() {
        AppointmentRequest req = new AppointmentRequest();
        req.setDoctorId(2L);
        LocalDateTime slot = LocalDateTime.now().plusDays(1);
        req.setAppointmentTime(slot);

        when(userRepo.findByEmail("p@test.com")).thenReturn(Optional.of(patient));
        when(userRepo.findById(2L)).thenReturn(Optional.of(doctor));
        when(appointmentRepo.isDoctorBooked(2L, slot)).thenReturn(true);

        assertThatThrownBy(() -> appointmentService.book("p@test.com", req))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("already booked");
    }

    @Test
    void updateStatus_success() {
        Appointment appt = Appointment.builder().id(1L)
            .status(AppointmentStatus.SCHEDULED).build();
        when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appt));
        when(appointmentRepo.save(any())).thenReturn(appt);

        Appointment result = appointmentService.updateStatus(1L, AppointmentStatus.COMPLETED);
        assertThat(result.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }
}
