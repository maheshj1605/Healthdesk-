package com.healthdesk.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EMRRequest {
    @NotNull(message = "Patient is required")
    private Long patientId;

    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;

    private String prescription;
    private String notes;
}
