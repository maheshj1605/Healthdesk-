package com.healthdesk.repository;

import com.healthdesk.model.EMR;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EMRRepository extends JpaRepository<EMR, Long> {
    List<EMR> findByPatientIdOrderByVisitDateDesc(Long patientId);
}
