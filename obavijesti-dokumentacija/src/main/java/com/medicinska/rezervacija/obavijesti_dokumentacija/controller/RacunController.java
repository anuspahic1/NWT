package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.RacunDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.RacunStatus;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.RacunService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/racuni")
@Validated
@Tag(name = "Računi", description = "REST API za upravljanje računima")
public class RacunController {

    @Autowired
    private RacunService racunService;

    @Operation(summary = "Dohvati sve račune", description = "Vraća listu svih računa")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista računa uspješno dohvaćena")
    })
    @GetMapping
    public ResponseEntity<List<RacunDTO>> getAllRacuni() {
        List<RacunDTO> racuni = racunService.getAllRacuni();
        return new ResponseEntity<>(racuni, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati račun po ID-u", description = "Vraća podatke o računu sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Račun pronađen"),
            @ApiResponse(responseCode = "404", description = "Račun nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<RacunDTO> getRacunById(@PathVariable("id") @NotNull Long racunID) {
        return racunService.getRacunById(racunID)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Kreiraj novi račun", description = "Kreira novi račun na osnovu poslanih podataka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Račun uspješno kreiran"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu (npr. nepostojeći pacijent/doktor)")
    })
    @PostMapping
    public ResponseEntity<RacunDTO> createRacun(@Valid @RequestBody RacunDTO racunDTO) {
        try {
            RacunDTO savedRacun = racunService.saveRacun(racunDTO);
            return new ResponseEntity<>(savedRacun, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @Operation(summary = "Ažuriraj račun", description = "Ažurira postojeći račun po ID-u")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Račun uspješno ažuriran"),
            @ApiResponse(responseCode = "404", description = "Račun nije pronađen"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu")
    })
    @PutMapping("/{id}")
    public ResponseEntity<RacunDTO> updateRacun(
            @PathVariable("id") @NotNull Long racunID,
            @Valid @RequestBody RacunDTO racunDTO) {
        try {
            return racunService.updateRacun(racunID, racunDTO)
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @Operation(summary = "Obriši račun", description = "Briše račun sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Račun uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Račun nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRacun(@PathVariable("id") @NotNull Long racunID) {
        if (racunService.deleteRacun(racunID)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Dohvati račune za pacijenta", description = "Vraća listu svih računa za određenog pacijenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista računa uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema računa za pacijenta")
    })
    @GetMapping("/pacijent/{pacijentId}")
    public ResponseEntity<List<RacunDTO>> getRacuniForPacijent(@PathVariable("pacijentId") @NotNull Long pacijentId) {
        List<RacunDTO> racuni = racunService.getRacuniForPacijent(pacijentId);
        return racuni.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(racuni, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati račune za doktora", description = "Vraća listu svih računa koje je izdao određeni doktor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista računa uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema računa od ovog doktora")
    })
    @GetMapping("/doktor/{doktorId}")
    public ResponseEntity<List<RacunDTO>> getRacuniForDoktor(@PathVariable("doktorId") @NotNull Long doktorId) {
        List<RacunDTO> racuni = racunService.getRacuniForDoktor(doktorId);
        return racuni.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(racuni, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati račune po statusu", description = "Vraća listu svih računa sa određenim statusom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista računa uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema računa sa ovim statusom")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<RacunDTO>> getRacuniByStatus(@PathVariable("status") @NotNull RacunStatus status) {
        List<RacunDTO> racuni = racunService.getRacuniByStatus(status);
        return racuni.isEmpty() ? new ResponseEntity<>(HttpStatus.NO_CONTENT) : new ResponseEntity<>(racuni, HttpStatus.OK);
    }

    @Operation(summary = "Označi račun kao plaćen", description = "Ažurira status računa na 'PLACEN' za zadani ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Račun uspješno označen kao plaćen"),
            @ApiResponse(responseCode = "404", description = "Račun nije pronađen"),
            @ApiResponse(responseCode = "400", description = "Račun je već plaćen")
    })
    @PutMapping("/{id}/oznaci-kao-placeno")
    public ResponseEntity<RacunDTO> markRacunAsPaid(@PathVariable("id") @NotNull Long id) {
        try {
            RacunDTO updatedRacun = racunService.markRacunAsPaid(id);
            return new ResponseEntity<>(updatedRacun, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }
}
