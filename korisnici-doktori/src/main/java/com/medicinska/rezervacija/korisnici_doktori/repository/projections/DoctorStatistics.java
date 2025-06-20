package com.medicinska.rezervacija.korisnici_doktori.repository.projections;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//ova klasa služi kao projekcija za rezultate SQL upita u repozitoriju
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DoctorStatistics {
    private String grad;
    private Long brojDoktora;
    private Double prosjecnaOcjena;
}
