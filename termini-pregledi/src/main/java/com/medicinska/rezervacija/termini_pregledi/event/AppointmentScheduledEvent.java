package com.medicinska.rezervacija.termini_pregledi.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentScheduledEvent {
    private Long appointmentId; //ID novokreiranog pregleda
    private Long patientId;     //ID pacijenta (Auth userId)
    private Long doctorId;      //ID doktora (Auth userId)
    private Long terminId;      //ID termina povezanog sa pregledom
    private String patientName; //Ime pacijenta za notifikaciju
    private String doctorName;  //Ime doktora za notifikaciju
    private LocalDateTime appointmentDateTime; //Datum i vrijeme pregleda
    private String appointmentReason; //Razlog pregleda
    private String correlationId; //ID za praćenje SAGA transakcije
}