package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.DoktorDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.repository.DoktorRepository;
import com.medicinska.rezervacija.korisnici_doktori.service.DoktorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doktori")
@RequiredArgsConstructor
public class DoktorController {

    private final DoktorRepository doktorRepository;
    private final DoktorService doktorService;

    @GetMapping
    public ResponseEntity<List<DoktorDTO>> getAllDoktori() {
        List<Doktor> doktori = doktorRepository.findAll();
        List<DoktorDTO> doktorDTOs = doktori.stream()
                .map(doktor -> DoktorDTO.builder()
                        .doktorID(doktor.getDoktorID())
                        .ime(doktor.getIme())
                        .prezime(doktor.getPrezime())
                        .email(doktor.getEmail())
                        .telefon(doktor.getTelefon())
                        .specijalizacije(doktor.getSpecijalizacije())
                        .grad(doktor.getGrad())
                        .userId(doktor.getUserId())
                        .radnoVrijeme(doktor.getRadnoVrijeme())
                        .iskustvo(doktor.getIskustvo())
                        .ocjena(doktor.getOcjena())
                        .profileImageBase64(doktor.getProfileImageBase64())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(doktorDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoktorDTO> getDoktorById(@PathVariable Integer id) {
        return doktorRepository.findById(id)
                .map(doktor -> ResponseEntity.ok(DoktorDTO.builder()
                        .doktorID(doktor.getDoktorID())
                        .ime(doktor.getIme())
                        .prezime(doktor.getPrezime())
                        .email(doktor.getEmail())
                        .telefon(doktor.getTelefon())
                        .specijalizacije(doktor.getSpecijalizacije())
                        .grad(doktor.getGrad())
                        .userId(doktor.getUserId())
                        .radnoVrijeme(doktor.getRadnoVrijeme())
                        .iskustvo(doktor.getIskustvo())
                        .ocjena(doktor.getOcjena())
                        .profileImageBase64(doktor.getProfileImageBase64())
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-auth-user/{authUserId}")
    public ResponseEntity<DoktorDTO> getDoktorByAuthUserId(@PathVariable Long authUserId) {
        return doktorRepository.findByUserId(authUserId)
                .map(doktor -> ResponseEntity.ok(DoktorDTO.builder()
                        .doktorID(doktor.getDoktorID())
                        .ime(doktor.getIme())
                        .prezime(doktor.getPrezime())
                        .email(doktor.getEmail())
                        .telefon(doktor.getTelefon())
                        .specijalizacije(doktor.getSpecijalizacije())
                        .grad(doktor.getGrad())
                        .userId(doktor.getUserId())
                        .radnoVrijeme(doktor.getRadnoVrijeme())
                        .iskustvo(doktor.getIskustvo())
                        .ocjena(doktor.getOcjena())
                        .profileImageBase64(doktor.getProfileImageBase64())
                        .build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/ocjena")
    public ResponseEntity<Void> updateDoktorOcjena(@PathVariable Integer id, @RequestBody Double ocjena) {
        Optional<Doktor> doktorOptional = doktorRepository.findById(id);
        if (doktorOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Doktor doktor = doktorOptional.get();
        doktor.setOcjena(ocjena);
        doktorRepository.save(doktor);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/recalculate-rating")
    public ResponseEntity<Void> recalculateDoktorRating(@PathVariable Integer id) {
        try {
            doktorService.recalculateAndSaveAverageRating(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error calculating grade for doctor ID " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/user-details/{userId}")
    public ResponseEntity<DoktorDTO> createOrUpdateDoktorByUserId(@PathVariable Long userId, @RequestBody DoktorDTO doktorDTO) {
        Optional<Doktor> existingDoktor = doktorRepository.findByUserId(userId);

        Doktor doktor;
        if (existingDoktor.isPresent()) {
            doktor = existingDoktor.get();
            doktor.setIme(doktorDTO.getIme());
            doktor.setPrezime(doktorDTO.getPrezime());
            doktor.setEmail(doktorDTO.getEmail());
            doktor.setTelefon(doktorDTO.getTelefon());
            doktor.setGrad(doktorDTO.getGrad());
            doktor.setSpecijalizacije(doktorDTO.getSpecijalizacije());
            doktor.setRadnoVrijeme(doktorDTO.getRadnoVrijeme());
            doktor.setIskustvo(doktorDTO.getIskustvo());
            doktor.setOcjena(doktorDTO.getOcjena());
            doktor.setProfileImageBase64(doktorDTO.getProfileImageBase64());
        } else {
            doktor = new Doktor();
            doktor.setUserId(userId);
            doktor.setIme(doktorDTO.getIme());
            doktor.setPrezime(doktorDTO.getPrezime());
            doktor.setEmail(doktorDTO.getEmail());
            doktor.setTelefon(doktorDTO.getTelefon());
            doktor.setGrad(doktorDTO.getGrad());
            doktor.setSpecijalizacije(doktorDTO.getSpecijalizacije());
            doktor.setRadnoVrijeme(doktorDTO.getRadnoVrijeme());
            doktor.setIskustvo(doktorDTO.getIskustvo());
            doktor.setOcjena(doktorDTO.getOcjena());
            doktor.setProfileImageBase64(doktorDTO.getProfileImageBase64());
        }
        Doktor savedDoktor = doktorRepository.save(doktor);
        return ResponseEntity.status(existingDoktor.isPresent() ? HttpStatus.OK : HttpStatus.CREATED)
                .body(DoktorDTO.builder()
                        .doktorID(savedDoktor.getDoktorID())
                        .ime(savedDoktor.getIme())
                        .prezime(savedDoktor.getPrezime())
                        .email(savedDoktor.getEmail())
                        .telefon(savedDoktor.getTelefon())
                        .grad(savedDoktor.getGrad())
                        .specijalizacije(savedDoktor.getSpecijalizacije())
                        .radnoVrijeme(savedDoktor.getRadnoVrijeme())
                        .iskustvo(savedDoktor.getIskustvo())
                        .ocjena(savedDoktor.getOcjena())
                        .userId(savedDoktor.getUserId())
                        .profileImageBase64(savedDoktor.getProfileImageBase64())
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoktorDTO> updateDoktor(@PathVariable Integer id, @RequestBody DoktorDTO doktorDTO) {
        return doktorRepository.findById(id)
                .map(existingDoktor -> {
                    existingDoktor.setIme(doktorDTO.getIme());
                    existingDoktor.setPrezime(doktorDTO.getPrezime());
                    existingDoktor.setEmail(doktorDTO.getEmail());
                    existingDoktor.setTelefon(doktorDTO.getTelefon());
                    existingDoktor.setGrad(doktorDTO.getGrad());
                    existingDoktor.setSpecijalizacije(doktorDTO.getSpecijalizacije());
                    existingDoktor.setRadnoVrijeme(doktorDTO.getRadnoVrijeme());
                    existingDoktor.setIskustvo(doktorDTO.getIskustvo());
                    existingDoktor.setOcjena(doktorDTO.getOcjena());
                    existingDoktor.setProfileImageBase64(doktorDTO.getProfileImageBase64());
                    Doktor updatedDoktor = doktorRepository.save(existingDoktor);
                    return ResponseEntity.ok(DoktorDTO.builder()
                            .doktorID(updatedDoktor.getDoktorID())
                            .ime(updatedDoktor.getIme())
                            .prezime(updatedDoktor.getPrezime())
                            .email(updatedDoktor.getEmail())
                            .telefon(updatedDoktor.getTelefon())
                            .grad(updatedDoktor.getGrad())
                            .specijalizacije(updatedDoktor.getSpecijalizacije())
                            .radnoVrijeme(updatedDoktor.getRadnoVrijeme())
                            .iskustvo(updatedDoktor.getIskustvo())
                            .ocjena(updatedDoktor.getOcjena())
                            .userId(updatedDoktor.getUserId())
                            .profileImageBase64(updatedDoktor.getProfileImageBase64())
                            .build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoktor(@PathVariable Integer id) {
        if (doktorRepository.existsById(id)) {
            doktorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
