package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.SpecijalizacijaDTO;
import com.medicinska.rezervacija.korisnici_doktori.exception.ErrorResponse;
import com.medicinska.rezervacija.korisnici_doktori.service.SpecijalizacijaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/specijalizacije")
@RequiredArgsConstructor
@Tag(name = "Kontroler za specijalizacije", description = "Upravljanje specijalizacijama doktora")
@Validated
public class SpecijalizacijaController {

    private final SpecijalizacijaService specijalizacijaService;

    @Operation(summary = "Dohvati sve specijalizacije sa paginacijom")
    @GetMapping("/paginated")
    public ResponseEntity<Page<SpecijalizacijaDTO>> sveSpecijalizacijePaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "nazivSpecijalizacije,asc") String[] sort) {

        return ResponseEntity.ok(specijalizacijaService.findAllPaginated(page, size, sort));
    }

    @Operation(summary = "Kreiraj specijalizaciju", description = "Dodaje novu specijalizaciju")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Specijalizacija kreirana"),
            @ApiResponse(responseCode = "400", description = "Nevalidni podaci",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<SpecijalizacijaDTO> kreirajSpecijalizaciju(
            @Valid @RequestBody SpecijalizacijaDTO specijalizacijaDTO) {
        return new ResponseEntity<>(specijalizacijaService.save(specijalizacijaDTO), HttpStatus.CREATED);
    }

    @Operation(summary = "Sve specijalizacije", description = "Vraća listu svih specijalizacija")
    @ApiResponse(responseCode = "200", description = "Specijalizacije pronađene")
    @GetMapping
    public ResponseEntity<List<SpecijalizacijaDTO>> sveSpecijalizacije() {
        return ResponseEntity.ok(specijalizacijaService.findAll());
    }

}
