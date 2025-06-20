package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.MedicinskaHistorijaDTO;
import com.medicinska.rezervacija.korisnici_doktori.service.MedicinskaHistorijaService;
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
@RequestMapping("/api/medicinska-historija")
@Validated
@Tag(name = "Medicinska Historija", description = "REST API za upravljanje medicinskom historijom pacijenata")
public class MedicinskaHistorijaController {

    @Autowired
    private MedicinskaHistorijaService medicinskaHistorijaService;

    @Operation(summary = "Dohvati sve zapise medicinske historije", description = "Vraća listu svih zapisa medicinske historije")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista zapisa uspješno dohvaćena")
    })
    @GetMapping
    public ResponseEntity<List<MedicinskaHistorijaDTO>> getAllMedicinskaHistorija() {
        List<MedicinskaHistorijaDTO> historija = medicinskaHistorijaService.getAllMedicinskaHistorija();
        return new ResponseEntity<>(historija, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati zapis medicinske historije po ID-u", description = "Vraća podatke o zapisu sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zapis pronađen"),
            @ApiResponse(responseCode = "404", description = "Zapis nije pronađen")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicinskaHistorijaDTO> getMedicinskaHistorijaById(@PathVariable("id") @NotNull Integer id) {
        return medicinskaHistorijaService.getMedicinskaHistorijaById(id)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Dohvati medicinsku historiju za određenog pacijenta", description = "Vraća listu svih zapisa medicinske historije za datog pacijenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista zapisa uspješno dohvaćena"),
            @ApiResponse(responseCode = "404", description = "Pacijent nije pronađen")
    })
    @GetMapping("/pacijent/{pacijentId}")
    public ResponseEntity<List<MedicinskaHistorijaDTO>> getMedicinskaHistorijaForPacijent(@PathVariable("pacijentId") @NotNull Integer pacijentId) {
        try {
            List<MedicinskaHistorijaDTO> historija = medicinskaHistorijaService.getMedicinskaHistorijaForPacijent(pacijentId);
            return new ResponseEntity<>(historija, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    @Operation(summary = "Kreiraj novi zapis medicinske historije", description = "Kreira novi zapis medicinske historije")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Zapis uspješno kreiran"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu (npr. nepostojeći pacijent/doktor)")
    })
    @PostMapping
    public ResponseEntity<MedicinskaHistorijaDTO> createMedicinskaHistorija(@Valid @RequestBody MedicinskaHistorijaDTO dto) {
        try {
            MedicinskaHistorijaDTO createdDto = medicinskaHistorijaService.createMedicinskaHistorija(dto);
            return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    @Operation(summary = "Ažuriraj zapis medicinske historije", description = "Ažurira postojeći zapis medicinske historije po ID-u")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zapis uspješno ažuriran"),
            @ApiResponse(responseCode = "404", description = "Zapis nije pronađen"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu")
    })
    @PutMapping("/{id}")
    public ResponseEntity<MedicinskaHistorijaDTO> updateMedicinskaHistorija(
            @PathVariable("id") @NotNull Integer id,
            @Valid @RequestBody MedicinskaHistorijaDTO dto) {
        try {
            MedicinskaHistorijaDTO updatedDto = medicinskaHistorijaService.updateMedicinskaHistorija(id, dto);
            return new ResponseEntity<>(updatedDto, HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(e.getStatusCode());
        }
    }

    @Operation(summary = "Obriši zapis medicinske historije", description = "Briše zapis sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Zapis uspješno obrisan"),
            @ApiResponse(responseCode = "404", description = "Zapis nije pronađen")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicinskaHistorija(@PathVariable("id") @NotNull Integer id) {
        if (medicinskaHistorijaService.deleteMedicinskaHistorija(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
