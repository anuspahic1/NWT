package com.medicinska.rezervacija.korisnici_doktori.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacijentDTO {
    private Integer pacijentID;

    private Long userId;

    @NotBlank(message = "Ime je obavezno.")
    @Size(min = 2, max = 50, message = "Ime mora imati između 2 i 50 karaktera.")
    private String ime;

    @NotBlank(message = "Prezime je obavezno.")
    @Size(min = 2, max = 50, message = "Prezime mora imati između 2 i 50 karaktera.")
    private String prezime;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Neispravan format email adrese.")
    private String email;

    @NotBlank(message = "Telefon je obavezan.")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Neispravan format telefona.")
    private String telefon;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate datumRodjenja;
    private String adresa;
}
