package com.medicinska.rezervacija.termini_pregledi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TerminDTO {

    private Integer terminID;

    @NotNull(message = "Doktor ID ne može biti null.")
    private Integer doktorID;

    @NotNull(message = "Datum termina je obavezan.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datum;

    @NotNull(message = "Vrijeme termina je obavezno.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime vrijeme;

    @NotNull(message = "Status termina mora biti naveden.")
    private StatusTermina statusTermina;
}
