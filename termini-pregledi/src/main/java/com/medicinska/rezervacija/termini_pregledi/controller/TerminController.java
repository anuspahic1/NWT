package com.medicinska.rezervacija.termini_pregledi.controller;

import com.medicinska.rezervacija.termini_pregledi.dto.TerminDTO;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import com.medicinska.rezervacija.termini_pregledi.service.TerminService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/termini")
@Tag(name = "Termin", description = "REST API za upravljanje terminima")
public class TerminController {

    @Autowired
    private TerminService terminService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @Operation(summary = "Dohvati sve termine", description = "Vraća listu svih termina")
    public ResponseEntity<List<TerminDTO>> getAllTermini() {
        List<TerminDTO> termini = terminService.getAllTermini();
        return new ResponseEntity<>(termini, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati termin po ID-u", description = "Vraća jedan termin na osnovu ID-a")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Termin pronađen"),
            @ApiResponse(responseCode = "404", description = "Termin nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TerminDTO> getTerminById(@PathVariable("id") Integer terminID) {
        Optional<TerminDTO> terminDTO = terminService.getTerminById(terminID);
        return terminDTO
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Kreiraj novi termin", description = "Kreira novi termin i vraća rezultat")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Termin uspješno kreiran"),
            @ApiResponse(responseCode = "400", description = "Greška prilikom slanja zahtjeva"),
            @ApiResponse(responseCode = "409", description = "Konflikt: Termin već postoji za odabrani datum i vrijeme ili je dostignut maksimalan broj termina")
    })
    @PostMapping
    public ResponseEntity<?> createTermin(@Valid @RequestBody TerminDTO terminDTO) {
        try {
            if (terminDTO.getStatusTermina() == null) {
                terminDTO.setStatusTermina(StatusTermina.DOSTUPAN);
            }
            TerminDTO savedTermin = terminService.saveTermin(terminDTO);
            return new ResponseEntity<>(savedTermin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Došlo je do neočekivane greške: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Ažuriraj termin", description = "Ažurira postojeći termin po ID-u")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Termin uspješno ažuriran"),
            @ApiResponse(responseCode = "404", description = "Termin nije pronađen"),
            @ApiResponse(responseCode = "400", description = "Greška prilikom slanja zahtjeva"),
            @ApiResponse(responseCode = "409", description = "Konflikt: Termin već postoji za odabrani datum i vrijeme")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTermin(@PathVariable("id") Integer terminID, @Valid @RequestBody TerminDTO terminDTO) {
        try {
            Optional<TerminDTO> updatedTermin = terminService.updateTermin(terminID, terminDTO);
            return updatedTermin.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Došlo je do neočekivane greške: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obriši termin", description = "Briše termin po ID-u")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Termin uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Termin nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTermin(@PathVariable("id") Integer terminID) {
        if (terminService.deleteTermin(terminID)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/slobodni/{doktorID}")
    @Operation(summary = "Dohvati slobodne termine za doktora", description = "Vraća listu svih slobodnih termina za određenog doktora")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista slobodnih termina"),
            @ApiResponse(responseCode = "404", description = "Doktor nije pronađen"),
            @ApiResponse(responseCode = "204", description = "Nema slobodnih termina")
    })
    public ResponseEntity<List<TerminDTO>> getSlobodniTerminiForDoktor(
            @PathVariable Integer doktorID) {
        List<TerminDTO> termini = terminService.getSlobodniTerminiForDoktor(doktorID);
        if(termini.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(termini, HttpStatus.OK);
    }

    @GetMapping("/dostupnost/{terminID}")
    @Operation(summary = "Provjeri dostupnost termina", description = "Provjerava da li je termin dostupan za zakazivanje")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dostupnost termina", content = @Content(schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Termin nije pronađen")
    })
    public ResponseEntity<Boolean> checkTerminAvailability(@PathVariable Integer terminID) {
         boolean isAvailable = terminService.checkTerminAvailability(terminID);
        return new ResponseEntity<>(isAvailable, HttpStatus.OK);
    }

    @GetMapping("/doktor/{doktorID}")
    @Operation(summary = "Dohvati termine za doktora", description = "Vraća listu svih termina za određenog doktora sa statusom dostupnosti, sortirano po datumu i vremenu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista termina"),
            @ApiResponse(responseCode = "404", description = "Doktor nije pronađen"),
            @ApiResponse(responseCode = "204", description = "Nema termina za doktora")
    })
    public ResponseEntity<List<TerminDTO>> getTerminiForDoktor(
            @PathVariable Integer doktorID) {
        List<TerminDTO> termini = terminService.getTerminiForDoktor(doktorID);
        if (termini.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(termini, HttpStatus.OK);
    }

    @GetMapping("/doktor/{doktorID}/datum/{datum}")
    @Operation(summary = "Dohvati dostupne termine za doktora na određeni datum", description = "Vraća listu slobodnih termina za određenog doktora i datum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista dostupnih termina"),
            @ApiResponse(responseCode = "204", description = "Nema dostupnih termina za dati datum i doktora")
    })
    public ResponseEntity<List<TerminDTO>> getAvailableTimesForDoctorAndDate(
            @PathVariable("doktorID") Integer doktorID,
            @PathVariable("datum") String datum) {
        List<TerminDTO> termini = terminService.getAvailableTimesForDoctorAndDate(doktorID, datum);
        if (termini.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(termini, HttpStatus.OK);
    }
}
