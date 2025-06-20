package com.medicinska.rezervacija.korisnici_doktori.service;

import com.medicinska.rezervacija.korisnici_doktori.dto.OcjenaDoktoraDTO;
import com.medicinska.rezervacija.korisnici_doktori.mapper.OcjenaDoktoraMapper;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.model.OcjenaDoktora;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import com.medicinska.rezervacija.korisnici_doktori.repository.DoktorRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.OcjenaDoktoraRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.PacijentRepository;
import com.medicinska.rezervacija.korisnici_doktori.remote.TerminiPreglediRemoteService;
import com.medicinska.rezervacija.korisnici_doktori.dto.PregledDTO; // Potrebno za komunikaciju
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
public class OcjenaDoktoraService {

    private final OcjenaDoktoraRepository ocjenaRepository;
    private final DoktorRepository doktorRepository;
    private final PacijentRepository pacijentRepository;
    private final OcjenaDoktoraMapper ocjenaMapper;
    private final DoktorService doktorService;
    private final TerminiPreglediRemoteService terminiPreglediRemoteService;

    public OcjenaDoktoraService(OcjenaDoktoraRepository ocjenaRepository,
                                DoktorRepository doktorRepository,
                                PacijentRepository pacijentRepository,
                                OcjenaDoktoraMapper ocjenaMapper,
                                DoktorService doktorService,
                                TerminiPreglediRemoteService terminiPreglediRemoteService) {
        this.ocjenaRepository = ocjenaRepository;
        this.doktorRepository = doktorRepository;
        this.pacijentRepository = pacijentRepository;
        this.ocjenaMapper = ocjenaMapper;
        this.doktorService = doktorService;
        this.terminiPreglediRemoteService = terminiPreglediRemoteService;
    }

    @Transactional(readOnly = true)
    public List<OcjenaDoktoraDTO> findAll() {
        return ocjenaRepository.findAll()
                .stream()
                .map(ocjenaMapper::toDto)
                .toList();
    }

    @Transactional
    public OcjenaDoktoraDTO save(OcjenaDoktoraDTO ocjenaDTO) {
        if (ocjenaDTO.getOcjena() == null || ocjenaDTO.getOcjena() < 1.0 || ocjenaDTO.getOcjena() > 5.0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ocjena mora biti između 1.0 i 5.0"
            );
        }
        if (ocjenaDTO.getPregledID() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ID pregleda je obavezan za ocjenu."
            );
        }

        // KLJUČNA IZMJENA: Provjera da li je ovaj pregled već ocijenjen od strane ovog pacijenta
        if (ocjenaRepository.existsByPacijent_PacijentIDAndPregledID(ocjenaDTO.getPacijentID(), ocjenaDTO.getPregledID())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ovaj pregled je već ocijenjen od strane ovog pacijenta."
            );
        }

        Doktor doktor = doktorRepository.findById(ocjenaDTO.getDoktorID())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Doktor sa ID " + ocjenaDTO.getDoktorID() + " nije pronađen"
                ));

        Pacijent pacijent = pacijentRepository.findById(ocjenaDTO.getPacijentID())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Pacijent sa ID " + ocjenaDTO.getPacijentID() + " nije pronađen"
                ));

        OcjenaDoktora ocjena = ocjenaMapper.toEntity(ocjenaDTO);
        ocjena.setDoktor(doktor);
        ocjena.setPacijent(pacijent);
        ocjena.setPregledID(ocjenaDTO.getPregledID()); // KLJUČNO: Postavite pregledID na entitet

        OcjenaDoktora savedOcjena = ocjenaRepository.save(ocjena);

        // --- AŽURIRANJE PREGLEDA U TERMINI_PREGLEDI MIKROSERVISU ---
        try {
            PregledDTO existingPregled = terminiPreglediRemoteService.getPregledById(ocjenaDTO.getPregledID());

            if (existingPregled != null) {
                existingPregled.setOcjenaDoktora(ocjenaDTO.getOcjena());
                terminiPreglediRemoteService.updatePregled(ocjenaDTO.getPregledID(), existingPregled);
                System.out.println("Ocjena " + ocjenaDTO.getOcjena() + " uspješno ažurirana u pregledu ID " + ocjenaDTO.getPregledID() + " u termini_pregledi mikroservisu.");
            } else {
                System.err.println("Upozorenje: Pregled sa ID " + ocjenaDTO.getPregledID() + " nije pronađen u termini_pregledi za ažuriranje ocjene. Ocjena nije povezana sa pregledom.");
            }
        } catch (Exception e) {
            System.err.println("Greška pri pokušaju ažuriranja ocjene u termini_pregledi za pregled ID " + ocjenaDTO.getPregledID() + ": " + e.getMessage());
            // Ovdje možete dodati logiku za obradu grešaka slanja notifikacije, npr. ponovni pokušaj ili fallback mehanizam.
        }
        // --- KRAJ NOVOG DIJELA ---

        doktorService.recalculateAndSaveAverageRating(doktor.getDoktorID());

        return ocjenaMapper.toDto(savedOcjena);
    }

    @Transactional(readOnly = true)
    public List<OcjenaDoktoraDTO> findByDoktorId(Integer doktorId) {
        return ocjenaRepository.findByDoktor_DoktorID(doktorId)
                .stream()
                .map(ocjenaMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double calculateAverageRating(Integer doktorId) {
        return ocjenaRepository.findByDoktor_DoktorID(doktorId)
                .stream()
                .mapToDouble(OcjenaDoktora::getOcjena)
                .average()
                .orElse(0.0);
    }

    // AŽURIRANO: Uklonjena logika za komentar, koristi samo između min i max ocjene
    @Transactional(readOnly = true)
    public List<OcjenaDoktoraDTO> findRatingsWithFilters(Double min, Double max) { // UKLONJEN Boolean hasComment
        double minRating = min != null ? min : 1.0;
        double maxRating = max != null ? max : 5.0;

        List<OcjenaDoktora> ocjene = ocjenaRepository.findByOcjenaBetween(minRating, maxRating);

        return ocjene.stream()
                .map(ocjenaMapper::toDto)
                .toList();
    }

    public Optional<OcjenaDoktoraDTO> findByPregledId(Integer pregledId) {
        return ocjenaRepository.findByPregledID(pregledId)
                .map(ocjenaMapper::toDto);
    }

}
