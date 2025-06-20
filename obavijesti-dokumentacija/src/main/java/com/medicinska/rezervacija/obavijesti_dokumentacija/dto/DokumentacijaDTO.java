package com.medicinska.rezervacija.obavijesti_dokumentacija.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DokumentacijaDTO {

    private Long dokumentacijaID;
    private Long pacijentID;
    private Long doktorID;
    private Long pregledID;
    private String tipDokumenta;
    private String nazivDokumenta;
    private Date datumKreiranja;
    private String pristup;
    private byte[] sadrzajDokumenta;

}