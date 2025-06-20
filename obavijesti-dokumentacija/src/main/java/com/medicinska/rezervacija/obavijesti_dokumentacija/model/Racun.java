package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "racun")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Racun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RacunID")
    private Long racunID;

    @Column(name = "PacijentID", nullable = false)
    private Long pacijentID; // ID pacijenta kojem je račun izdan

    @Column(name = "DoktorID", nullable = false)
    private Long doktorID;

    @Column(name = "DatumIzdavanja", nullable = false)
    private LocalDate datumIzdavanja;

    @Column(name = "Iznos", nullable = false)
    private Double iznos;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private RacunStatus status;

    @Column(name = "Opis", columnDefinition = "TEXT")
    private String opis;

    @CreationTimestamp
    @Column(name = "TimestampKreiranja", nullable = false, updatable = false)
    private LocalDateTime timestampKreiranja;
}
