package com.medicinska.rezervacija.obavijesti_dokumentacija.dto;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotifikacijaDTO {

    private Long notifikacijaID;
    private String sadrzaj;
    private String status;
    private String datumSlanja;
    private Long korisnikID;
    private String uloga;

    public NotifikacijaDTO(Notifikacija notifikacija) {
        this.notifikacijaID = notifikacija.getNotifikacijaID();
        this.sadrzaj = notifikacija.getSadrzaj();
        this.status = notifikacija.getStatus().toString();
        this.datumSlanja = notifikacija.getDatumSlanja().toString();
        this.korisnikID = notifikacija.getKorisnikID();
        this.uloga = notifikacija.getUloga().toString();
    }

}