package com.medicinska.rezervacija.obavijesti_dokumentacija.clients;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "KORISNICI-DOKTORI")
public interface KorisniciDoktoriClient {

    @GetMapping("/korisnici-doktori/api/pacijenti/{pacijentId}")
    PacijentDTO getPacijentById(@PathVariable("pacijentId") Long pacijentId);

    @GetMapping("/korisnici-doktori/api/doktori/{doktorId}")
    DoktorDTO getDoktorById(@PathVariable("doktorId") Long doktorId);
}
