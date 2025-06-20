package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.OcjenaDoktoraDTO;
import com.medicinska.rezervacija.korisnici_doktori.exception.ErrorResponse;
import com.medicinska.rezervacija.korisnici_doktori.service.OcjenaDoktoraService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ocjene")
@RequiredArgsConstructor
@Validated
@Tag(name = "Ocjena Doktora Controller", description = "Upravljanje ocjenama doktora")
public class OcjenaDoktoraController {

    private final OcjenaDoktoraService ocjenaService;

    @Operation(summary = "Dodaj ocjenu", description = "Dodaje novu ocjenu za doktora")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ocjena kreirana"),
            @ApiResponse(responseCode = "400", description = "Nevalidni podaci",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Doktor ili pacijent nije pronađen",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<OcjenaDoktoraDTO> dodajOcjenu(
            @Valid @RequestBody OcjenaDoktoraDTO ocjenaDTO) {
        ocjenaDTO.setOcjenaID(null);
        return new ResponseEntity<>(ocjenaService.save(ocjenaDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Ocjene za doktora", description = "Vraća listu svih ocjena za doktora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista ocjena vraćena"),
            @ApiResponse(responseCode = "404", description = "Doktor nije pronađen",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/doktor/{doktorId}")
    public ResponseEntity<List<OcjenaDoktoraDTO>> ocjeneZaDoktora(@PathVariable Integer doktorId) {
        return ResponseEntity.ok(ocjenaService.findByDoktorId(doktorId));
    }

    @Operation(summary = "Prosjek ocjena", description = "Računa prosjek za određenog doktora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Prosjek izračunat"),
            @ApiResponse(responseCode = "404", description = "Doktor nije pronađen ili nema ocjena",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/doktor/{doktorId}/prosjek")
    public ResponseEntity<Double> prosjekOcjena(@PathVariable Integer doktorId) {
        return ResponseEntity.ok(ocjenaService.calculateAverageRating(doktorId));
    }

    @Operation(summary = "Filtriranje ocjena")
    @GetMapping("/filter")
    public ResponseEntity<List<OcjenaDoktoraDTO>> filterRatings(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max,
            @RequestParam(required = false) Boolean hasComment) {
        return ResponseEntity.ok(ocjenaService.findRatingsWithFilters(min, max));
    }

    @GetMapping("/pregled/{pregledId}")
    public ResponseEntity<OcjenaDoktoraDTO> getByPregledId(@PathVariable Integer pregledId) {
        return ocjenaService.findByPregledId(pregledId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
