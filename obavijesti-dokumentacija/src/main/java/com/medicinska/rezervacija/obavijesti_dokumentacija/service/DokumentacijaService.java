package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.clients.KorisniciDoktoriClient;
import com.medicinska.rezervacija.obavijesti_dokumentacija.clients.TerminiPreglediClient;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PregledDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DokumentacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Dokumentacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DokumentacijaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DokumentacijaService {

    private final DokumentacijaRepository dokumentacijaRepository;
    private final ModelMapper modelMapper;
    private final KorisniciDoktoriClient korisniciDoktoriClient;
    private final TerminiPreglediClient terminiPreglediClient;

    @Autowired
    public DokumentacijaService(DokumentacijaRepository dokumentacijaRepository,
                                ModelMapper modelMapper,
                                KorisniciDoktoriClient korisniciDoktoriClient,
                                TerminiPreglediClient terminiPreglediClient) {
        this.dokumentacijaRepository = dokumentacijaRepository;
        this.modelMapper = modelMapper;
        this.korisniciDoktoriClient = korisniciDoktoriClient;
        this.terminiPreglediClient = terminiPreglediClient;
    }

    public DokumentacijaDTO createDokumentacija(DokumentacijaDTO dokumentacijaDTO) {
        if (dokumentacijaDTO.getPacijentID() != null) {
            try {
                PacijentDTO pacijent = korisniciDoktoriClient.getPacijentById(dokumentacijaDTO.getPacijentID());
                if (pacijent == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pacijent sa ID " + dokumentacijaDTO.getPacijentID() + " nije pronađen.");
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Greška u komunikaciji sa KorisniciDoktori servisom za pacijenta: " + e.getMessage(), e);
            }
        }

        if (dokumentacijaDTO.getDoktorID() != null) {
            try {
                DoktorDTO doktor = korisniciDoktoriClient.getDoktorById(dokumentacijaDTO.getDoktorID());
                if (doktor == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doktor sa ID " + dokumentacijaDTO.getDoktorID() + " nije pronađen.");
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Greška u komunikaciji sa KorisniciDoktori servisom za doktora: " + e.getMessage(), e);
            }
        }

        if (dokumentacijaDTO.getPregledID() != null) {
            try {
                PregledDTO pregled = terminiPreglediClient.getPregledById(dokumentacijaDTO.getPregledID());
                if (pregled == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregled sa ID " + dokumentacijaDTO.getPregledID() + " nije pronađen.");
                }
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Greška u komunikaciji sa TerminiPregledi servisom za pregled: " + e.getMessage(), e);
            }
        }

        Dokumentacija dokumentacija = modelMapper.map(dokumentacijaDTO, Dokumentacija.class);
        dokumentacija.setDatumKreiranja(new Date());
        Dokumentacija savedDokumentacija = dokumentacijaRepository.save(dokumentacija);
        return modelMapper.map(savedDokumentacija, DokumentacijaDTO.class);
    }

    public DokumentacijaDTO getDokumentacijaById(Long id) {
        Dokumentacija dokumentacija = dokumentacijaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dokumentacija sa ID " + id + " nije pronađena."));
        return modelMapper.map(dokumentacija, DokumentacijaDTO.class);
    }

    public List<DokumentacijaDTO> getDokumentacijaByPacijentId(Long pacijentId) {
        List<Dokumentacija> dokumenti = dokumentacijaRepository.findByPacijentID(pacijentId);
        return dokumenti.stream()
                .map(doc -> modelMapper.map(doc, DokumentacijaDTO.class))
                .collect(Collectors.toList());
    }

    public List<DokumentacijaDTO> getDokumentacijaByDoktorId(Long doktorId) {
        List<Dokumentacija> dokumenti = dokumentacijaRepository.findByDoktorID(doktorId);
        return dokumenti.stream()
                .map(doc -> modelMapper.map(doc, DokumentacijaDTO.class))
                .collect(Collectors.toList());
    }

    public DokumentacijaDTO uploadDocument(MultipartFile file, Optional<Long> pacijentID, Optional<Long> doktorID, String tipDokumenta, String pristup) throws IOException {
        System.out.println("DEBUG: Pokrenut uploadDocument metoda.");
        System.out.println("DEBUG: Pacijent ID: " + pacijentID.orElse(null) + ", Doktor ID: " + doktorID.orElse(null) + ", Tip Dokumenta: " + tipDokumenta + ", Pristup: " + pristup);
        System.out.println("DEBUG: Naziv fajla: " + file.getOriginalFilename() + ", Veličina fajla: " + file.getSize() + " bytes");

        if (pacijentID.isEmpty() && doktorID.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Morate osigurati ili Pacijent ID ili Doktor ID za upload dokumenta.");
        }

        Dokumentacija dokumentacija = new Dokumentacija();
        dokumentacija.setNazivDokumenta(file.getOriginalFilename());
        dokumentacija.setTipDokumenta(tipDokumenta);
        dokumentacija.setPristup(Dokumentacija.Pristup.valueOf(pristup.toUpperCase()));
        dokumentacija.setDatumKreiranja(new Date());

        if (pacijentID.isPresent()) {
            dokumentacija.setPacijentID(pacijentID.get());
            try {
                PacijentDTO pacijent = korisniciDoktoriClient.getPacijentById(pacijentID.get());
                if (pacijent == null) {
                    System.err.println("ERROR: Pacijent sa ID " + pacijentID.get() + " nije pronađen.");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pacijent sa ID " + pacijentID.get() + " nije pronađen.");
                }
                System.out.println("DEBUG: Pacijent sa ID " + pacijentID.get() + " uspješno pronađen.");
            } catch (Exception e) {
                System.err.println("ERROR: Greška u komunikaciji sa KorisniciDoktori servisom za pacijenta (upload): " + e.getMessage());
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Greška u komunikaciji sa KorisniciDoktori servisom za pacijenta: " + e.getMessage(), e);
            }
        }

        if (doktorID.isPresent()) {
            dokumentacija.setDoktorID(doktorID.get());
            try {
                DoktorDTO doktor = korisniciDoktoriClient.getDoktorById(doktorID.get());
                if (doktor == null) {
                    System.err.println("ERROR: Doktor sa ID " + doktorID.get() + " nije pronađen.");
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Doktor sa ID " + doktorID.get() + " nije pronađen.");
                }
                System.out.println("DEBUG: Doktor sa ID " + doktorID.get() + " uspješno pronađen.");
            } catch (Exception e) {
                System.err.println("ERROR: Greška u komunikaciji sa KorisniciDoktori servisom za doktora (upload): " + e.getMessage());
                e.printStackTrace();
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Greška u komunikaciji sa KorisniciDoktori servisom za doktora: " + e.getMessage(), e);
            }
        }

        dokumentacija.setPregledID(null);
        System.out.println("DEBUG: Pregled ID postavljen na null.");

        try {
            dokumentacija.setSadrzajDokumenta(file.getBytes());
            System.out.println("DEBUG: Sadržaj fajla uspješno pročitan u bajtove. Veličina bajtova: " + dokumentacija.getSadrzajDokumenta().length + " bytes.");
        } catch (IOException e) {
            System.err.println("ERROR: Greška prilikom čitanja bajtova fajla: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Greška prilikom čitanja bajtova fajla.", e);
        }

        Dokumentacija savedDokumentacija;
        try {
            savedDokumentacija = dokumentacijaRepository.save(dokumentacija);
            System.out.println("DEBUG: Dokument uspješno pohranjen u bazu sa ID: " + savedDokumentacija.getDokumentacijaID());
        } catch (Exception e) {
            System.err.println("ERROR: Greška prilikom pohranjivanja dokumenta u bazu: " + e.getMessage());
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Greška prilikom pohranjivanja dokumenta: " + e.getMessage(), e);
        }

        return modelMapper.map(savedDokumentacija, DokumentacijaDTO.class);
    }

    public DokumentacijaDTO downloadDocument(Long id) {
        Dokumentacija dokumentacija = dokumentacijaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dokument sa ID " + id + " nije pronađen."));

        DokumentacijaDTO dto = modelMapper.map(dokumentacija, DokumentacijaDTO.class);
        dto.setSadrzajDokumenta(dokumentacija.getSadrzajDokumenta());

        return dto;
    }
}
