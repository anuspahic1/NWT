package com.medicinska.rezervacija.korisnici_doktori.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecijalizacijaDTO {
    private Integer specijalizacijaID;
    private List<String> doktori;

    @NotBlank(message = "Naziv specijalizacije je obavezan")
    @Size(max = 100, message = "Naziv može imati najviše 100 karaktera")
    private String naziv;
}
