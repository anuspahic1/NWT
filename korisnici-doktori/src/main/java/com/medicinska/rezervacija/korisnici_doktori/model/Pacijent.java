package com.medicinska.rezervacija.korisnici_doktori.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pacijent")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pacijent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PacijentID")
    private Integer pacijentID;

    @Column(name = "UserID", unique = true, nullable = false)
    private Long userId;

    @NotBlank
    @Size(max = 50)
    private String ime;

    @NotBlank
    @Size(max = 50)
    private String prezime;

    @Column(unique = true)
    @NotBlank
    @Size(max = 100)
    private String email;

    @Pattern(regexp = "\\+?[0-9. ()-]{7,25}$", message = "Telefon mora biti u validnom formatu")
    private String telefon;

    @Column(name = "DatumRodjenja")
    private LocalDate datumRodjenja;

    @Column(name = "Adresa")
    private String adresa;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "pacijent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<OcjenaDoktora> ocjene = new ArrayList<>();

    public Pacijent(Long userId, String ime, String prezime, String telefon, String email, LocalDate datumRodjenja, String adresa) {
        this.userId = userId;
        this.ime = ime;
        this.prezime = prezime;
        this.telefon = telefon;
        this.email = email;
        this.datumRodjenja = datumRodjenja;
        this.adresa = adresa;
        this.notes = notes;
    }
}
