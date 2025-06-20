package com.medicinska.rezervacija.termini_pregledi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoktorDTO {
    private Integer doktorID;
    private Long userId;
    private String ime;
    private String prezime;
    private String email;
    private String telefon;
    private List<String> specijalizacije;
    private String grad;
    private String radnoVrijeme;
    private Integer iskustvo;
    private Double ocjena;
    private String profileImageBase64;
}
