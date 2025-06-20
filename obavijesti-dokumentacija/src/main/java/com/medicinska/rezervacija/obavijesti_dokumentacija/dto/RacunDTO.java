package com.medicinska.rezervacija.obavijesti_dokumentacija.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RacunDTO {
    private Long racunID;
    private Long pacijentID;
    private String pacijentIme;
    private Long doktorID;
    private String doktorIme;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datumIzdavanja;
    private Double iznos;
    private String status;
    private String opis;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestampKreiranja;
}
