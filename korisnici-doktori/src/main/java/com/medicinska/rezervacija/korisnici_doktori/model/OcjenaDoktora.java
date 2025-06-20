package com.medicinska.rezervacija.korisnici_doktori.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ocjene_doktora", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"pacijent_id", "doktor_id", "pregled_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcjenaDoktora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ocjenaID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pacijent_id", nullable = false)
    private Pacijent pacijent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doktor_id", nullable = false)
    private Doktor doktor;

    @Column(name = "ocjena", nullable = false)
    private Double ocjena; // Brojčana ocjena (1.0-5.0)

    @Column(name = "pregled_id", nullable = false)
    private Integer pregledID;

}
