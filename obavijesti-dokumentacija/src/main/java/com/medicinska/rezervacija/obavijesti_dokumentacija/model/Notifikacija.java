package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notifikacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notifikacijaID;

    @NotNull(message = "Korisnik ID je obavezan")
    private Long korisnikID;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Uloga je obavezna")
    private Uloga uloga;

    @NotBlank(message = "Sadržaj notifikacije je obavezan")
    private String sadrzaj;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status notifikacije je obavezan")
    private StatusNotifikacije status;

    @PastOrPresent(message = "Datum slanja ne može biti u budućnosti")
    private LocalDateTime datumSlanja;

    public enum Uloga {
        PACIJENT, DOKTOR
    }

    public enum StatusNotifikacije {
        POSLANO, PROCITANO
    }
}