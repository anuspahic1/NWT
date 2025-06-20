package com.medicinska.rezervacija.termini_pregledi.service;

import com.medicinska.rezervacija.termini_pregledi.dto.TerminDTO;
import com.medicinska.rezervacija.termini_pregledi.model.Termin;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import com.medicinska.rezervacija.termini_pregledi.repository.TerminRepository;
import com.medicinska.rezervacija.termini_pregledi.remote.KorisniciDoktoriRemoteService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TerminService {

    @Autowired
    private TerminRepository terminRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private KorisniciDoktoriRemoteService korisniciDoktoriRemoteService;

    @Value("${app.termin.max-termini-per-day:10}")
    private int maxTerminiPerDay;

    public List<TerminDTO> getAllTermini() {
        return terminRepository.findAll().stream()
                .map(termin -> modelMapper.map(termin, TerminDTO.class))
                .collect(Collectors.toList());
    }

    public Optional<TerminDTO> getTerminById(Integer terminID) {
        return terminRepository.findById(terminID)
                .map(termin -> modelMapper.map(termin, TerminDTO.class));
    }

    public TerminDTO saveTermin(TerminDTO terminDTO) {
        if (!korisniciDoktoriRemoteService.doesDoktorExist(terminDTO.getDoktorID())) {
            throw new IllegalArgumentException("Doktor sa ID-em " + terminDTO.getDoktorID() + " ne postoji.");
        }

        LocalDate datum = terminDTO.getDatum();
        LocalTime vrijeme = terminDTO.getVrijeme();

        System.out.println("DEBUG (saveTermin): Datum: " + datum + ", Vrijeme: " + vrijeme);

        Optional<Termin> existing = terminRepository.findByDoktorIDAndDatumAndVrijeme(
                terminDTO.getDoktorID(), datum, vrijeme
        );

        if (existing.isPresent()) {
            throw new IllegalStateException("Termin već postoji za tog doktora u datom terminu.");
        }

        long brojPostojecih = terminRepository.countZauzetihTerminaByDoktorAndDatum(
                terminDTO.getDoktorID(), datum
        );

        if (brojPostojecih >= maxTerminiPerDay) {
            throw new IllegalStateException("Maksimalan broj termina dostignut za " + datum);
        }

        Termin termin = modelMapper.map(terminDTO, Termin.class);
        Termin sacuvan = terminRepository.save(termin);

        return modelMapper.map(sacuvan, TerminDTO.class);
    }

    public Optional<TerminDTO> updateTermin(Integer terminID, TerminDTO terminDTO) {
        return terminRepository.findById(terminID).map(existing -> {
            if (!korisniciDoktoriRemoteService.doesDoktorExist(terminDTO.getDoktorID())) {
                throw new IllegalArgumentException("Doktor sa ID-em " + terminDTO.getDoktorID() + " ne postoji.");
            }

            LocalDate datum = terminDTO.getDatum();
            LocalTime vrijeme = terminDTO.getVrijeme();

            Optional<Termin> duplikat = terminRepository.findByDoktorIDAndDatumAndVrijeme(
                    terminDTO.getDoktorID(), datum, vrijeme
            );

            if (duplikat.isPresent() && !duplikat.get().getTerminID().equals(terminID)) {
                throw new IllegalStateException("Već postoji termin u tom vremenu za doktora.");
            }

            existing.setDoktorID(terminDTO.getDoktorID());
            existing.setDatum(datum);
            existing.setVrijeme(vrijeme);
            existing.setStatusTermina(terminDTO.getStatusTermina());

            Termin azuriran = terminRepository.save(existing);
            return modelMapper.map(azuriran, TerminDTO.class);
        });
    }

    public boolean deleteTermin(Integer terminID) {
        if (terminRepository.existsById(terminID)) {
            terminRepository.deleteById(terminID);
            return true;
        }
        return false;
    }

    public List<TerminDTO> getSlobodniTerminiForDoktor(Integer doktorID) {
        return terminRepository.findByDoktorIDAndStatusTermina(doktorID, StatusTermina.DOSTUPAN)
                .stream()
                .map(t -> modelMapper.map(t, TerminDTO.class))
                .collect(Collectors.toList());
    }

    public boolean checkTerminAvailability(Integer terminID) {
        return terminRepository.findById(terminID)
                .map(t -> t.getStatusTermina() == StatusTermina.DOSTUPAN)
                .orElse(false);
    }

    public List<TerminDTO> getTerminiForDoktor(Integer doktorID) {
        return terminRepository.findByDoktorID(doktorID)
                .stream()
                .map(t -> modelMapper.map(t, TerminDTO.class))
                .collect(Collectors.toList());
    }

    public List<TerminDTO> getAvailableTimesForDoctorAndDate(Integer doktorID, String datum) {
        LocalDate parsed = LocalDate.parse(datum);

        return terminRepository.findByDoktorIDAndDatum(doktorID, parsed)
                .stream()
                .filter(t -> t.getStatusTermina() == StatusTermina.DOSTUPAN)
                .map(t -> modelMapper.map(t, TerminDTO.class))
                .collect(Collectors.toList());
    }
}
