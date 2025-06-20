package com.medicinska.rezervacija.korisnici_doktori.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PacijentNotesDTO {
    private String notes;
}
