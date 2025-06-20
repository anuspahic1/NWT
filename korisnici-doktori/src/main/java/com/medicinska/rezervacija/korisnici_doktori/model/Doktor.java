package com.medicinska.rezervacija.korisnici_doktori.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "doktor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doktor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer doktorID;

    @Column(name = "userid", nullable = false, unique = true)
    private Long userId;

    @Column(name = "ime", length = 50)
    private String ime;

    @Column(name = "prezime", length = 50)
    private String prezime;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "telefon", length = 20)
    private String telefon;

    @Column(name = "grad", length = 100)
    private String grad;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "doktor_specijalizacije", joinColumns = @JoinColumn(name = "doktor_id"))
    @Column(name = "specijalizacija_naziv", length = 100)
    private List<String> specijalizacije;

    @Column(name = "radno_vrijeme", length = 100)
    private String radnoVrijeme;

    @Column(name = "iskustvo")
    private Integer iskustvo;

    @Column(name = "ocjena")
    private Double ocjena;

    @Column(columnDefinition = "TEXT")
    private String profileImageBase64;
}
