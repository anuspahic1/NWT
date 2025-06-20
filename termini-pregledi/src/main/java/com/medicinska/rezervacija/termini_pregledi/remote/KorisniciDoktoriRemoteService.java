package com.medicinska.rezervacija.termini_pregledi.remote;

import com.medicinska.rezervacija.termini_pregledi.dto.DoktorDTO;
import com.medicinska.rezervacija.termini_pregledi.dto.PacijentDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.Optional;

@FeignClient(name = "KORISNICI-DOKTORI", path = "/korisnici-doktori")
public interface KorisniciDoktoriRemoteService {

    @GetMapping("/api/doktori/{doktorId}")
    Optional<DoktorDTO> getDoktorById(@PathVariable("doktorId") Integer doktorId);

    @GetMapping("/api/pacijenti/{pacijentId}")
    Optional<PacijentDTO> getPacijentById(@PathVariable("pacijentId") Integer pacijentId);

    @PutMapping("/api/doktori/{doktorId}/recalculate-rating")
    void triggerDoktorRatingRecalculation(@PathVariable("doktorId") Integer doktorId);

    default boolean doesDoktorExist(Integer doktorId) {
        return getDoktorById(doktorId).isPresent();
    }

    default boolean doesPacijentExist(Integer pacijentId) {
        return getPacijentById(pacijentId).isPresent();
    }

    @GetMapping("/api/ocjene/pregled/{pregledId}")
    Optional<com.medicinska.rezervacija.termini_pregledi.dto.OcjenaDoktoraDTO> getOcjenaZaPregled(
            @PathVariable("pregledId") Integer pregledId);

}
