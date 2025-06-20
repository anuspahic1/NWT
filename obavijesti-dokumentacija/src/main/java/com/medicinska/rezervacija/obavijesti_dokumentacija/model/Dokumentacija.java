package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dokumentacija {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dokumentacijaID;

    private Long pacijentID;
    private Long doktorID;
    private Long pregledID;

    @NotBlank(message = "Tip dokumenta je obavezan")
    private String tipDokumenta;

    @NotBlank(message = "Naziv dokumenta je obavezan")
    private String nazivDokumenta;

    @PastOrPresent(message = "Datum kreiranja ne može biti u budućnosti")
    @Temporal(TemporalType.TIMESTAMP)
    private Date datumKreiranja;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Pristup dokumentaciji je obavezan")
    private Pristup pristup;

    @Lob
    @Column(name = "SadrzajDokumenta", columnDefinition = "LONGBLOB")
    private byte[] sadrzajDokumenta;

    public enum Pristup {
        PRIVATNA, JAVNA
    }
}