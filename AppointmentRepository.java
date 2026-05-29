package com.healthdesk.repository;

import com.healthdesk.model.Appointment;
import com.healthdesk.model.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientIdOrderByAppointmentTimeDesc(Long patientId);
    List<Appointment> findByDoctorIdOrderByAppointmentTimeDesc(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end " +
           "AND a.status = com.healthdesk.model.AppointmentStatus.SCHEDULED")
    List<Appointment> findDoctorSchedule(@Param("doctorId") Long doctorId,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime = :appointmentTime " +
           "AND a.status = com.healthdesk.model.AppointmentStatus.SCHEDULED")
    boolean isDoctorBooked(@Param("doctorId") Long doctorId,
                           @Param("appointmentTime") LocalDateTime appointmentTime);
}
