package com.medicinska.rezervacija.korisnici_doktori.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "medicinska_historija")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicinskaHistorija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ZapisID")
    private Integer zapisID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PacijentID", nullable = false)
    @JsonIgnore // Izbjegava rekurentnu serializaciju
    private Pacijent pacijent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DoktorID", nullable = false)
    @JsonIgnore
    private Doktor doktor;

    @Column(name = "DatumZapisivanja", nullable = false)
    private LocalDate datumZapisivanja;

    @Column(name = "Dijagnoza", columnDefinition = "TEXT", nullable = false)
    private String dijagnoza;

    @Column(name = "Lijecenje", columnDefinition = "TEXT")
    private String lijecenje;

    @Column(name = "Napomene", columnDefinition = "TEXT")
    private String napomene;
}
