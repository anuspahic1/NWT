package com.medicinska.rezervacija.korisnici_doktori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicinskaHistorijaDTO {
    private Integer zapisID;
    private Integer pacijentID;
    private String pacijentIme;
    private Integer doktorID;
    private String doktorIme;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datumZapisivanja;
    private String dijagnoza;
    private String lijecenje;
    private String napomene;
}
