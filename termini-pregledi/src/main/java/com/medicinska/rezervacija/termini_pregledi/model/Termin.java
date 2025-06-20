package com.medicinska.rezervacija.termini_pregledi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "termin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Termin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TerminID")
    private Integer terminID;

    @Column(name = "DoktorID", nullable = false)
    private Integer doktorID;

    @Column(name = "Datum", nullable = false)
    private LocalDate datum;

    @Column(name = "Vrijeme", nullable = false)
    private LocalTime vrijeme;

    @Enumerated(EnumType.STRING)
    @Column(name = "StatusTermina", nullable = false)
    private StatusTermina statusTermina;

    @OneToMany(
            mappedBy = "termin",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<Pregled> pregledi = new ArrayList<>();

}