package com.medicinska.rezervacija.korisnici_doktori.controller;

import com.medicinska.rezervacija.korisnici_doktori.dto.PacijentDTO;
import com.medicinska.rezervacija.korisnici_doktori.dto.PacijentNotesDTO;
import com.medicinska.rezervacija.korisnici_doktori.service.PacijentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@RequestMapping("/api/pacijenti")
@RequiredArgsConstructor
public class PacijentController {

    private final PacijentService pacijentService;

    @GetMapping
    public ResponseEntity<List<PacijentDTO>> getAllPacijenti() {
        List<PacijentDTO> pacijentDTOs = pacijentService.findAll();
        return ResponseEntity.ok(pacijentDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PacijentDTO> getPacijentById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(pacijentService.findById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/by-auth-user/{authUserId}")
    public ResponseEntity<PacijentDTO> getPacijentByAuthUserId(@PathVariable Long authUserId) {
        try {
            return ResponseEntity.ok(pacijentService.getPacijentByUserId(authUserId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping
    public ResponseEntity<PacijentDTO> addPacijent(@Valid @RequestBody PacijentDTO pacijentDTO) {
        PacijentDTO createdPacijent = pacijentService.save(pacijentDTO);
        return new ResponseEntity<>(createdPacijent, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PacijentDTO> updatePacijent(@PathVariable Integer id, @Valid @RequestBody PacijentDTO pacijentDTO) { // Dodano @Valid
        try {
            PacijentDTO updatedPacijent = pacijentService.update(id, pacijentDTO);
            return ResponseEntity.ok(updatedPacijent);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePacijent(@PathVariable Integer id) {
        try {
            pacijentService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{pacijentId}/notes")
    public ResponseEntity<PacijentNotesDTO> getPacijentNotes(@PathVariable Integer pacijentId) {
        try {
            PacijentNotesDTO notes = pacijentService.getPacijentNotes(pacijentId);
            return ResponseEntity.ok(notes);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{pacijentId}/notes")
    public ResponseEntity<Void> updatePacijentNotes(@PathVariable Integer pacijentId, @RequestBody PacijentNotesDTO notesDTO) {
        try {
            pacijentService.updatePacijentNotes(pacijentId, notesDTO);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
