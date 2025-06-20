package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.OdgovorNaPorukuDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PorukaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.PorukaService;
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
@RequestMapping("/api/poruke")
@Validated
@Tag(name = "Poruke", description = "REST API za upravljanje sigurnim porukama")
public class PorukaController {

    @Autowired
    private PorukaService porukaService;

    @Operation(summary = "Dohvati sve poruke", description = "Vraća listu svih poruka")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista poruka uspješno dohvaćena")
    })
    @GetMapping
    public ResponseEntity<List<PorukaDTO>> getAllPoruke() {
        List<PorukaDTO> poruke = porukaService.getAllPoruke();
        return new ResponseEntity<>(poruke, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati poruku po ID-u", description = "Vraća podatke o poruci sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Poruka pronađena"),
            @ApiResponse(responseCode = "404", description = "Poruka nije pronađena")
    })
    @GetMapping("/{id}")
    public ResponseEntity<PorukaDTO> getPorukaById(@PathVariable("id") @NotNull Long porukaID) {
        return porukaService.getPorukaById(porukaID)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Pošalji novu poruku", description = "Kreira i šalje novu poruku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Poruka uspješno poslana"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu (npr. nepostojeći pošiljatelj/primatelj)")
    })
    @PostMapping("/posalji")
    public ResponseEntity<PorukaDTO> sendPoruka(@Valid @RequestBody PorukaDTO porukaDTO) {
        try {
            PorukaDTO sentPoruka = porukaService.sendPoruka(porukaDTO);
            return new ResponseEntity<>(sentPoruka, HttpStatus.CREATED);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @Operation(summary = "Odgovori na poruku", description = "Dodaje odgovor na postojeću poruku")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Odgovor uspješno dodan"),
            @ApiResponse(responseCode = "404", description = "Glavna poruka nije pronađena"),
            @ApiResponse(responseCode = "400", description = "Neispravni podaci u zahtjevu")
    })
    @PostMapping("/{parentMessageId}/odgovori")
    public ResponseEntity<PorukaDTO> replyToPoruka(
            @PathVariable("parentMessageId") @NotNull Long parentMessageId,
            @Valid @RequestBody OdgovorNaPorukuDTO odgovorDTO) {
        try {
            return porukaService.replyToPoruka(parentMessageId, odgovorDTO)
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(null, e.getStatusCode());
        }
    }

    @Operation(summary = "Dohvati primljene poruke za korisnika", description = "Vraća listu poruka koje je korisnik primio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista primljenih poruka uspješno dohvaćena")
    })
    @GetMapping("/primatelj/{receiverId}")
    public ResponseEntity<List<PorukaDTO>> getReceivedMessages(
            @PathVariable("receiverId") @NotNull Long receiverId,
            @RequestParam("receiverType") @NotNull Notifikacija.Uloga receiverType) {
        List<PorukaDTO> poruke = porukaService.getReceivedMessages(receiverId, receiverType);
        return new ResponseEntity<>(poruke, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati poslane poruke od korisnika", description = "Vraća listu poruka koje je korisnik poslao")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista poslanih poruka uspješno dohvaćena")
    })
    @GetMapping("/poslano/{senderId}")
    public ResponseEntity<List<PorukaDTO>> getSentMessages(
            @PathVariable("senderId") @NotNull Long senderId,
            @RequestParam("senderType") @NotNull Notifikacija.Uloga senderType) {
        List<PorukaDTO> poruke = porukaService.getSentMessages(senderId, senderType);
        return new ResponseEntity<>(poruke, HttpStatus.OK);
    }

    @Operation(summary = "Dohvati cijelu konverzaciju za korisnika", description = "Vraća sve poruke i odgovore u kojima je korisnik sudjelovao")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista poruka uspješno dohvaćena")
    })
    @GetMapping("/konverzacija/{userId}")
    public ResponseEntity<List<PorukaDTO>> getConversation(
            @PathVariable("userId") @NotNull Long userId,
            @RequestParam("userType") @NotNull Notifikacija.Uloga userType) {
        List<PorukaDTO> konverzacija = porukaService.getConversation(userId, userType);
        return new ResponseEntity<>(konverzacija, HttpStatus.OK);
    }

    @Operation(summary = "Obriši poruku", description = "Briše poruku sa zadanim ID-om")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Poruka uspješno obrisana"),
            @ApiResponse(responseCode = "404", description = "Poruka nije pronađena")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoruka(@PathVariable("id") @NotNull Long porukaID) {
        if (porukaService.deletePoruka(porukaID)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Ažuriraj poruku", description = "Ažurira postojeću poruku po ID-u")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Poruka uspješno ažurirana"),
            @ApiResponse(responseCode = "404", description = "Poruka nije pronađena")
    })
    @PutMapping("/{id}")
    public ResponseEntity<PorukaDTO> updatePoruka(
            @PathVariable("id") @NotNull Long porukaID,
            @Valid @RequestBody PorukaDTO porukaDTO) {
        return porukaService.updatePoruka(porukaID, porukaDTO)
                .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
