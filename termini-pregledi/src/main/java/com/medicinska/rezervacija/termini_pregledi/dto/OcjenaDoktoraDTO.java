package com.medicinska.rezervacija.termini_pregledi.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OcjenaDoktoraDTO {

    private Integer ocjenaID;

    @NotNull(message = "ID pacijenta je obavezan")
    private Integer pacijentID;

    @NotNull(message = "ID doktora je obavezan")
    private Integer doktorID;

    @NotNull(message = "ID pregleda je obavezan za ocjenu")
    private Integer pregledID;

    @NotNull(message = "Ocjena je obavezna")
    @DecimalMin(value = "1.0", message = "Ocjena mora biti najmanje 1.0")
    @DecimalMax(value = "5.0", message = "Ocjena može biti najviše 5.0")
    private Double ocjena;

}
