package com.medicinska.rezervacija.korisnici_doktori.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "specijalizacija")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specijalizacija {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SpecijalizacijaID")
    private Integer specijalizacijaID;

    @NotBlank
    @Size(max = 100)
    @Column(name = "NazivSpecijalizacije", nullable = false, unique = true)
    private String nazivSpecijalizacije;

}