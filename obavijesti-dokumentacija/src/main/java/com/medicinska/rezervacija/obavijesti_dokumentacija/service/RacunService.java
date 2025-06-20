package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.RacunDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Racun;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.RacunStatus;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.RacunRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class RacunService {

    @Autowired
    private RacunRepository racunRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final String KORISNICI_DOKTORI_BASE_URL = "http://KORISNICI-DOKTORI/korisnici-doktori/api";

    private String getKorisnikIme(Long id, String type) {
        String ime = "N/A";
        if (id == null) return ime;

        try {
            if ("PACIJENT".equalsIgnoreCase(type)) {
                PacijentDTO pacijent = restTemplate.getForObject(
                        KORISNICI_DOKTORI_BASE_URL + "/pacijenti/" + id,
                        PacijentDTO.class
                );
                if (pacijent != null) {
                    ime = pacijent.getIme() + " " + pacijent.getPrezime();
                }
            } else if ("DOKTOR".equalsIgnoreCase(type)) {
                DoktorDTO doktor = restTemplate.getForObject(
                        KORISNICI_DOKTORI_BASE_URL + "/doktori/" + id,
                        DoktorDTO.class
                );
                if (doktor != null) {
                    ime = doktor.getIme() + " " + doktor.getPrezime();
                }
            }
        } catch (HttpClientErrorException.NotFound ex) {
            System.err.println("Korisnik sa ID-em " + id + " i tipom " + type + " nije pronađen.");
        } catch (Exception ex) {
            System.err.println("Greška prilikom dohvaćanja imena korisnika (ID: " + id + ", Tip: " + type + "): " + ex.getMessage());
        }
        return ime;
    }

    private RacunDTO toRacunDTO(Racun racun) {
        if (racun == null) return null;
        RacunDTO dto = modelMapper.map(racun, RacunDTO.class);

        dto.setPacijentIme(getKorisnikIme(racun.getPacijentID(), "PACIJENT"));
        dto.setDoktorIme(getKorisnikIme(racun.getDoktorID(), "DOKTOR"));

        dto.setStatus(racun.getStatus() != null ? racun.getStatus().name() : null);

        return dto;
    }

    private Racun toRacunEntity(RacunDTO racunDTO) {
        if (racunDTO == null) return null;
        Racun racun = modelMapper.map(racunDTO, Racun.class);
        racun.setStatus(racunDTO.getStatus() != null ? RacunStatus.valueOf(racunDTO.getStatus()) : null);
        return racun;
    }


    public List<RacunDTO> getAllRacuni() {
        return racunRepository.findAll().stream()
                .map(this::toRacunDTO)
                .collect(Collectors.toList());
    }

    public Optional<RacunDTO> getRacunById(Long racunID) {
        return racunRepository.findById(racunID)
                .map(this::toRacunDTO);
    }

    @Transactional
    public RacunDTO saveRacun(RacunDTO racunDTO) {
        // Provjeri postojanje pacijenta i doktora
        if (getKorisnikIme(racunDTO.getPacijentID(), "PACIJENT").equals("N/A") ||
                getKorisnikIme(racunDTO.getDoktorID(), "DOKTOR").equals("N/A")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pacijent ili doktor ne postoji.");
        }

        Racun racun = toRacunEntity(racunDTO);
        if (racun.getDatumIzdavanja() == null) {
            racun.setDatumIzdavanja(LocalDate.now());
        }
        if (racun.getStatus() == null) {
            racun.setStatus(RacunStatus.NEPLACEN);
        }

        Racun savedRacun = racunRepository.save(racun);
        return toRacunDTO(savedRacun);
    }

    @Transactional
    public Optional<RacunDTO> updateRacun(Long racunID, RacunDTO updatedRacunDTO) {
        return Optional.ofNullable(racunRepository.findById(racunID)
                .map(existingRacun -> {
                    if (!existingRacun.getPacijentID().equals(updatedRacunDTO.getPacijentID()) && getKorisnikIme(updatedRacunDTO.getPacijentID(), "PACIJENT").equals("N/A")) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ažurirani pacijent ne postoji.");
                    }
                    if (!existingRacun.getDoktorID().equals(updatedRacunDTO.getDoktorID()) && getKorisnikIme(updatedRacunDTO.getDoktorID(), "DOKTOR").equals("N/A")) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ažurirani doktor ne postoji.");
                    }

                    existingRacun.setPacijentID(updatedRacunDTO.getPacijentID());
                    existingRacun.setDoktorID(updatedRacunDTO.getDoktorID());
                    existingRacun.setDatumIzdavanja(updatedRacunDTO.getDatumIzdavanja());
                    existingRacun.setIznos(updatedRacunDTO.getIznos());
                    existingRacun.setStatus(RacunStatus.valueOf(updatedRacunDTO.getStatus()));
                    existingRacun.setOpis(updatedRacunDTO.getOpis());

                    Racun savedRacun = racunRepository.save(existingRacun);
                    return toRacunDTO(savedRacun);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Račun s ID-em " + racunID + " nije pronađen za ažuriranje.")));
    }

    @Transactional
    public boolean deleteRacun(Long racunID) {
        if (racunRepository.existsById(racunID)) {
            racunRepository.deleteById(racunID);
            return true;
        }
        return false;
    }

    public List<RacunDTO> getRacuniForPacijent(Long pacijentID) {
        return racunRepository.findByPacijentIDOrderByDatumIzdavanjaDesc(pacijentID).stream()
                .map(this::toRacunDTO)
                .collect(Collectors.toList());
    }

    public List<RacunDTO> getRacuniForDoktor(Long doktorID) {
        return racunRepository.findByDoktorIDOrderByDatumIzdavanjaDesc(doktorID).stream()
                .map(this::toRacunDTO)
                .collect(Collectors.toList());
    }

    public List<RacunDTO> getRacuniByStatus(RacunStatus status) {
        return racunRepository.findByStatusOrderByDatumIzdavanjaDesc(status).stream()
                .map(this::toRacunDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RacunDTO markRacunAsPaid(Long racunID) {
        Racun racun = racunRepository.findById(racunID)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Račun sa ID-em " + racunID + " nije pronađen."));

        if (racun.getStatus() == RacunStatus.PLACEN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Račun sa ID-em " + racunID + " je već plaćen.");
        }

        racun.setStatus(RacunStatus.PLACEN);
        Racun updatedRacun = racunRepository.save(racun);
        return toRacunDTO(updatedRacun);
    }
}
