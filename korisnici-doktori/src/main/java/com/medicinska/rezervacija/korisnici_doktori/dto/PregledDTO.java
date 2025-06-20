package com.medicinska.rezervacija.korisnici_doktori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
// Uklonjeni importi za java.sql.Date i java.sql.Time
import java.time.LocalDate; // Ovi importi nisu potrebni u DTO ako su polja String
import java.time.LocalTime; // Ovi importi nisu potrebni u DTO ako su polja String

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PregledDTO {
    private Integer pregledID;

    @NotNull(message = "Datum pregleda je obavezan.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Format datuma pregleda mora biti YYYY-MM-DD.") // Dodao pattern validaciju za String datum
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String datumPregleda; // PROMIJENJENO NA STRING

    @NotNull(message = "Vrijeme pregleda je obavezno.")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d):([0-5]\\d)$", message = "Format vremena pregleda mora biti HH:MM:SS.") // Dodao pattern validaciju za String vrijeme
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private String vrijemePregleda; // PROMIJENJENO NA STRING

    @Pattern(regexp = "zakazan|obavljen|otkazan|u_toku", message = "Status pregleda može biti samo 'zakazan', 'obavljen', 'otkazan' ili 'u_toku'.")
    private String status;

    @DecimalMin(value = "1.0", message = "Ocjena mora biti najmanje 1")
    @DecimalMax(value = "5.0", message = "Ocjena ne može biti veća od 5")
    private Double ocjenaDoktora;

    @Size(max = 500, message = "Komentar pacijenta ne može imati više od 500 karaktera.")
    private String komentarPacijenta;

    @NotNull(message = "ID pacijenta je obavezan.")
    private Integer pacijentID;

    @NotNull(message = "ID doktora je obavezan.")
    private Integer doktorID;

    @NotNull(message = "ID termina je obavezan.")
    private Integer terminID;

    private String pacijentIme;
    private String doktorIme;
}
