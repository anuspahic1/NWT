package com.medicinska.rezervacija.obavijesti_dokumentacija.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentScheduledEvent {
    private Long appointmentId; // ID novokreiranog pregleda
    private Long patientId;     // ID pacijenta (Auth userId)
    private Long doctorId;      // ID doktora (Auth userId)
    private Long terminId;      // NOVO: ID termina povezanog sa pregledom
    private String patientName;
    private String doctorName;
    private LocalDateTime appointmentDateTime;
    private String appointmentReason;
    private String correlationId;
}