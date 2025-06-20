package com.medicinska.rezervacija.termini_pregledi.controller;

import com.medicinska.rezervacija.termini_pregledi.dto.PregledDTO;
import com.medicinska.rezervacija.termini_pregledi.model.Pregled;
import com.medicinska.rezervacija.termini_pregledi.service.PregledService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pregledi")
@Validated
@Tag(name = "Pregledi", description = "REST API za upravljanje pregledima")
public class PregledController {

    @Autowired
    private PregledService pregledService;

    @Operation(summary = "Dohvati sve preglede", description = "Vraća listu svih pregleda")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena")
    })
    @GetMapping
    public ResponseEntity<List<PregledDTO>> getAllPregledi() {
        List<PregledDTO> pregledi = pregledService.getAllPregledi();
        return new ResponseEntity<>(pregledi, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Kreiraj novi pregled (Asinkrono)", description = "Kreira novi pregled na osnovu poslanih podataka. Odgovor je asinkron.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Zahtjev za pregled prihvaćen za obradu"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu ili termin zauzet"),
            @ApiResponse(responseCode = "404", description = "Termin nije pronađen")
    })
    public ResponseEntity<String> createPregled(@Valid @RequestBody PregledDTO pregledDTO) {
        pregledService.createPregledAndPublishEvent(pregledDTO);
        return ResponseEntity.accepted().body("Zahtjev za zakazivanje pregleda je uspješno prihvaćen i obrađuje se asinkrono. Dobit ćete obavijest kada bude završen.");
    }

    @Operation(summary = "Dohvati pregled po ID-u", description = "Vraća podatke o pregledu sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregled pronađen"),
            @ApiResponse(responseCode = "404", description = "Pregled nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PregledDTO> getPregledById(@PathVariable("id") @NotNull Integer pregledID) {
        Optional<PregledDTO> pregledDTO = pregledService.getPregledById(pregledID);
        return pregledDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Dohvati preglede po statusu", description = "Vraća sve preglede koji imaju određeni status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena"),
            @ApiResponse(responseCode = "400", description = "Neispravan status")
    })
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PregledDTO>> getPreglediByStatus(@PathVariable("status") Pregled.Status status) {
        List<PregledDTO> pregledDTOs = pregledService.getPreglediByStatus(status);
        return new ResponseEntity<>(pregledDTOs, HttpStatus.OK);
    }

    @Operation(summary = "Ažuriraj pregled", description = "Ažurira pregled na osnovu ID-a i novih podataka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pregled uspješno ažuriran"),
            @ApiResponse(responseCode = "404", description = "Pregled nije pronađen"),
            @ApiResponse(responseCode = "400", description = "Greška u validaciji")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PregledDTO> updatePregled(
            @PathVariable("id") @NotNull Integer pregledID,
            @Valid @RequestBody PregledDTO pregledDTO) {
        Optional<PregledDTO> updatedPregledDTO = pregledService.updatePregled(pregledID, pregledDTO);
        return updatedPregledDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Obriši pregled", description = "Briše pregled sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pregled uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Pregled nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePregled(@PathVariable("id") @NotNull Integer pregledID) {
        if (pregledService.deletePregled(pregledID)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/doktor/{doktorId}/ratings")
    @Operation(summary = "Dohvati sve ocjene pregleda za doktora",
            description = "Vraća listu ocjena (Double) za sve preglede određenog doktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista ocjena uspješno dohvaćena", content = @Content(schema = @Schema(type = "array", implementation = Double.class))),
            @ApiResponse(responseCode = "204", description = "Nema ocjena za doktora")
    })
    public ResponseEntity<List<Double>> getDoctorRatings(@PathVariable Integer doktorId) {
        List<Double> ratings = pregledService.getPreglediForDoktor(doktorId).stream()
                // ISPRAVKA: Ukloniti filter za null. Ako je ocjena 0.0, ona je validna ocjena.
                // Provjeriti da li je ocjenaDoktora tipa Double (wrapper) u Pregled entitetu i DTO-u
                // Ako je primitivni double, onda null provjera nije potrebna
                .filter(p -> p.getOcjenaDoktora() != null) // Keep this if ocjenaDoktora can be null
                .map(PregledDTO::getOcjenaDoktora)
                .collect(Collectors.toList());

        // Ažurirano: ako nema ocjena ili su sve null, vratite 204.
        if (ratings.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    @GetMapping("/zakazani/{pacijentID}")
    @Operation(summary = "Dohvati zakazane preglede za pacijenta",
            description = "Vraća listu svih zakazanih pregleda za određenog pacijenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista zakazanih pregleda"),
            @ApiResponse(responseCode = "404", description = "Pacijent nije pronađen")
    })
    public ResponseEntity<List<PregledDTO>> getZakazaniPreglediForPacijent(
            @PathVariable Integer pacijentID) {

        List<PregledDTO> pregledi = pregledService.getZakazaniPreglediForPacijent(pacijentID);

        if (pregledi.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(pregledi, HttpStatus.OK);
    }
    @GetMapping("/pacijent/{pacijentID}")
    @Operation(summary = "Dohvati sve preglede za pacijenta",
            description = "Vraća listu svih pregleda (zakazanih, obavljenih, otkazanih) za određenog pacijenta.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema pregleda za ovog pacijenta")
    })
    public ResponseEntity<List<PregledDTO>> getPreglediForPacijent(
            @PathVariable Integer pacijentID) {
        List<PregledDTO> pregledi = pregledService.getPreglediForPacijent(pacijentID);
        return pregledi.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(pregledi, HttpStatus.OK);
    }

    @GetMapping("/doktor/{doktorID}/status/{status}")
    @Operation(summary = "Dohvati preglede za doktora po statusu",
            description = "Vraća listu pregleda za određenog doktora sa zadanim statusom")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema pregleda")
    })
    public ResponseEntity<List<PregledDTO>> getPreglediForDoktorByStatus(
            @PathVariable Integer doktorID,
            @PathVariable Pregled.Status status) {
        List<PregledDTO> pregledi = pregledService.getPreglediForDoktorByStatus(doktorID, status);
        return pregledi.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(pregledi, HttpStatus.OK);
    }

    @GetMapping("/datum/{datum}")
    @Operation(summary = "Dohvati preglede za određeni datum",
            description = "Vraća listu pregleda za zadani datum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema pregleda za taj datum")
    })
    public ResponseEntity<List<PregledDTO>> getPreglediByDate(
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date datum) {
        java.sql.Date sqlDate = new java.sql.Date(datum.getTime());
        List<PregledDTO> pregledi = pregledService.getPreglediByDate(sqlDate.toLocalDate());
        return pregledi.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(pregledi, HttpStatus.OK);
    }

    @GetMapping("/raspon")
    @Operation(summary = "Dohvati preglede u vremenskom rasponu",
            description = "Vraća listu pregleda sa zadanim statusom u datom vremenskom rasponu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista pregleda uspješno dohvaćena"),
            @ApiResponse(responseCode = "204", description = "Nema pregleda u datom rasponu"),
            @ApiResponse(responseCode = "400", description = "Neispravni parametri datuma")
    })
    public ResponseEntity<List<PregledDTO>> getPreglediInDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") java.util.Date endDate,
            @RequestParam Pregled.Status status) {
        java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
        java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());

        if (sqlStartDate.after(sqlEndDate)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<PregledDTO> pregledi = pregledService
                .getPreglediInDateRange(sqlStartDate.toLocalDate(), sqlEndDate.toLocalDate(), status);
        return pregledi.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(pregledi, HttpStatus.OK);
    }

    @GetMapping("/doktor/{doktorID}/count")
    @Operation(summary = "Broj pregleda po doktoru",
            description = "Vraća ukupan broj pregleda za određenog doktora")
    @ApiResponse(responseCode = "200", description = "Broj pregleda uspješno dohvaćen")
    public ResponseEntity<Long> countPreglediByDoktor(@PathVariable Integer doktorID) {
        long count = pregledService.countPreglediByDoktor(doktorID);
        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}
