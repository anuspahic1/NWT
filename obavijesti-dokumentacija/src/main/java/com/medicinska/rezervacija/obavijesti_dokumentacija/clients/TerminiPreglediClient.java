package com.medicinska.rezervacija.obavijesti_dokumentacija.clients;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PregledDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.TerminDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "termini-pregledi", path = "/api/termini-pregledi")
public interface TerminiPreglediClient {

     @GetMapping("/api/pregledi/{pregledId}")
    PregledDTO getPregledById(@PathVariable("pregledId") Long pregledId);

      @GetMapping("/api/termini/doktor/{doktorId}/datum/{datum}")
    List<TerminDTO> getAvailableTermsForDoctorAndDate(
            @PathVariable("doktorId") Integer doktorId,
            @PathVariable("datum") String datum
    );
}