package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.UserRegisteredEventDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import com.medicinska.rezervacija.korisnici_doktori.repository.DoktorRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.PacijentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;


@RestController
@RequestMapping("/api/internal/user-sync")
@RequiredArgsConstructor
public class UserSyncController {

    private final DoktorRepository doktorRepository;
    private final PacijentRepository pacijentRepository;

    @PostMapping("/registered")
    @Transactional
    public ResponseEntity<Void> handleUserRegisteredEvent(@RequestBody UserRegisteredEventDTO event) {
        System.out.println("Received UserRegisteredEvent: " + event.toString());

        Set<String> roles = event.getRoles();
        System.out.println("User ID: " + event.getUserId() + ", Email: " + event.getEmail() + ", Roles received: " + roles);
        System.out.println("Specijalizacije received from Auth Service: " + event.getSpecijalizacije());
        System.out.println("Ocjena received from Auth Service: " + event.getOcjena());

        Optional<Doktor> existingDoktor = doktorRepository.findByUserId(event.getUserId());
        Optional<Pacijent> existingPacijent = pacijentRepository.findByUserId(event.getUserId());

        if (existingDoktor.isPresent()) {
            System.out.println("User with userId " + event.getUserId() + " already exists as Doktor. Skipping creation.");
            return ResponseEntity.ok().build();
        }
        if (existingPacijent.isPresent()) {
            System.out.println("User with userId " + event.getUserId() + " already exists as Pacijent. Skipping creation.");
            return ResponseEntity.ok().build();
        }

        if (roles != null && roles.contains("ROLE_DOKTOR")) {
            Doktor doktor = Doktor.builder()
                    .userId(event.getUserId())
                    .ime(event.getIme())
                    .prezime(event.getPrezime())
                    .email(event.getEmail())
                    .telefon(event.getTelefon())
                    .grad(event.getGrad())
                    .specijalizacije(event.getSpecijalizacije() != null ? event.getSpecijalizacije() : new ArrayList<>())
                    .radnoVrijeme(event.getRadnoVrijeme())
                    .iskustvo(event.getIskustvo())
                    .ocjena(event.getOcjena())
                    .build();
            doktorRepository.save(doktor);

            System.out.println("Doktor created and specializations/rating saved for userId: " + event.getUserId() + " - " + doktor.getIme() + " " + doktor.getPrezime());
            System.out.println("Doktor's saved specializations: " + doktor.getSpecijalizacije());
            System.out.println("Doktor's saved rating: " + doktor.getOcjena());

        } else if (roles != null && roles.contains("ROLE_PACIJENT")) {
            Pacijent pacijent = Pacijent.builder()
                    .userId(event.getUserId())
                    .ime(event.getIme())
                    .prezime(event.getPrezime())
                    .telefon(event.getTelefon())
                    .email(event.getEmail())
                    .build();
            pacijentRepository.save(pacijent);
            System.out.println("Pacijent created for userId: " + event.getUserId() + " - " + pacijent.getIme() + " " + pacijent.getPrezime());
        } else {
            System.out.println("No specific role (ROLE_DOKTOR/ROLE_PACIJENT) found for userId: " + event.getUserId() + ". Roles: " + roles + ". Skipping creation.");
        }
        return ResponseEntity.ok().build();
    }
}
