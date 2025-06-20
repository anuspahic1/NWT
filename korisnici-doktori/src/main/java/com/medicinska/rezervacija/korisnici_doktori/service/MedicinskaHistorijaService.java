package com.medicinska.rezervacija.korisnici_doktori.service;

import com.medicinska.rezervacija.korisnici_doktori.dto.MedicinskaHistorijaDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.model.MedicinskaHistorija;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import com.medicinska.rezervacija.korisnici_doktori.repository.DoktorRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.MedicinskaHistorijaRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.PacijentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MedicinskaHistorijaService {

    @Autowired
    private MedicinskaHistorijaRepository medicinskaHistorijaRepository;

    @Autowired
    private PacijentRepository pacijentRepository;

    @Autowired
    private DoktorRepository doktorRepository;

    @Autowired
    private ModelMapper modelMapper;

    private MedicinskaHistorijaDTO toDTO(MedicinskaHistorija entity) {
        if (entity == null) return null;
        MedicinskaHistorijaDTO dto = modelMapper.map(entity, MedicinskaHistorijaDTO.class);
        dto.setPacijentID(entity.getPacijent().getPacijentID());
        dto.setPacijentIme(entity.getPacijent().getIme() + " " + entity.getPacijent().getPrezime());
        dto.setDoktorID(entity.getDoktor().getDoktorID());
        dto.setDoktorIme(entity.getDoktor().getIme() + " " + entity.getDoktor().getPrezime());
        return dto;
    }

    public List<MedicinskaHistorijaDTO> getAllMedicinskaHistorija() {
        return medicinskaHistorijaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<MedicinskaHistorijaDTO> getMedicinskaHistorijaById(Integer id) {
        return medicinskaHistorijaRepository.findById(id)
                .map(this::toDTO);
    }

    public List<MedicinskaHistorijaDTO> getMedicinskaHistorijaForPacijent(Integer pacijentID) {
        Pacijent pacijent = pacijentRepository.findById(pacijentID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pacijent sa ID-em " + pacijentID + " nije pronađen."));
        return medicinskaHistorijaRepository.findByPacijentOrderByDatumZapisivanjaDesc(pacijent).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public MedicinskaHistorijaDTO createMedicinskaHistorija(MedicinskaHistorijaDTO dto) {
        Pacijent pacijent = pacijentRepository.findById(dto.getPacijentID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pacijent sa ID-em " + dto.getPacijentID() + " nije pronađen."));
        Doktor doktor = doktorRepository.findById(dto.getDoktorID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doktor sa ID-em " + dto.getDoktorID() + " nije pronađen."));

        MedicinskaHistorija novaHistorija = modelMapper.map(dto, MedicinskaHistorija.class);
        novaHistorija.setPacijent(pacijent);
        novaHistorija.setDoktor(doktor);
        if (novaHistorija.getDatumZapisivanja() == null) {
            novaHistorija.setDatumZapisivanja(LocalDate.now());
        }

        MedicinskaHistorija savedHistorija = medicinskaHistorijaRepository.save(novaHistorija);
        return toDTO(savedHistorija);
    }

    public MedicinskaHistorijaDTO updateMedicinskaHistorija(Integer id, MedicinskaHistorijaDTO dto) {
        MedicinskaHistorija existingHistorija = medicinskaHistorijaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Zapis medicinske historije sa ID-em " + id + " nije pronađen."));

        Pacijent pacijent = pacijentRepository.findById(dto.getPacijentID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pacijent sa ID-em " + dto.getPacijentID() + " nije pronađen."));
        Doktor doktor = doktorRepository.findById(dto.getDoktorID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Doktor sa ID-em " + dto.getDoktorID() + " nije pronađen."));

        existingHistorija.setPacijent(pacijent);
        existingHistorija.setDoktor(doktor);
        existingHistorija.setDatumZapisivanja(dto.getDatumZapisivanja());
        existingHistorija.setDijagnoza(dto.getDijagnoza());
        existingHistorija.setLijecenje(dto.getLijecenje());
        existingHistorija.setNapomene(dto.getNapomene());

        MedicinskaHistorija updatedHistorija = medicinskaHistorijaRepository.save(existingHistorija);
        return toDTO(updatedHistorija);
    }

    public boolean deleteMedicinskaHistorija(Integer id) {
        if (medicinskaHistorijaRepository.existsById(id)) {
            medicinskaHistorijaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
