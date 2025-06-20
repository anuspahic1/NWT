package com.medicinska.rezervacija.termini_pregledi.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate; // DODANO: Import za LocalDate
import java.time.LocalTime; // DODANO: Import za LocalTime


@Entity
@Table(name = "pregled")
@Data
@NoArgsConstructor
@AllArgsConstructor // DODANO
@Builder
public class Pregled {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PregledID")
    private Integer pregledID;

    @Column(name = "PacijentID", nullable = false)
    private Integer pacijentId;

    @Column(name = "DoktorID", nullable = false)
    private Integer doktorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TerminID", referencedColumnName = "TerminID", nullable = false)
    private Termin termin;

    // KLJUČNA IZMJENA: Promijenjeno sa java.sql.Date na LocalDate
    @Column(name = "DatumPregleda", nullable = false)
    private LocalDate datumPregleda;

    // KLJUČNA IZMJENA: Promijenjeno sa java.sql.Time na LocalTime
    @Column(name = "VrijemePregleda", nullable = false)
    private LocalTime vrijemePregleda;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private Status status;

    @Column(name = "OcjenaDoktora")
    private Double ocjenaDoktora;

    @Column(name = "KomentarPacijenta")
    private String komentarPacijenta;

    public enum Status {
        zakazan("zakazan"),
        obavljen("obavljen"),
        otkazan("otkazan"),
        u_toku("u_toku"); // DODANO: Uključen "u_toku" status ako ga koristite

        private final String name;

        private Status(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
