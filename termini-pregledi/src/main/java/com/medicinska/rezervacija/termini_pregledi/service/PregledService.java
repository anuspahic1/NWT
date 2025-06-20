package com.medicinska.rezervacija.termini_pregledi.service;

import com.medicinska.rezervacija.termini_pregledi.clients.NotificationClient;
import com.medicinska.rezervacija.termini_pregledi.dto.DoktorDTO;
import com.medicinska.rezervacija.termini_pregledi.dto.PacijentDTO;
import com.medicinska.rezervacija.termini_pregledi.dto.PregledDTO;
import com.medicinska.rezervacija.termini_pregledi.model.Pregled;
import com.medicinska.rezervacija.termini_pregledi.model.Termin;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import com.medicinska.rezervacija.termini_pregledi.repository.PregledRepository;
import com.medicinska.rezervacija.termini_pregledi.repository.TerminRepository;
import com.medicinska.rezervacija.termini_pregledi.remote.KorisniciDoktoriRemoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PregledService {

    private final PregledRepository pregledRepository;
    private final TerminRepository terminRepository;
    private final KorisniciDoktoriRemoteService korisniciDoktoriRemoteService;
    private final NotificationClient notificationClient;

    @Autowired
    public PregledService(PregledRepository pregledRepository,
                          TerminRepository terminRepository,
                          KorisniciDoktoriRemoteService korisniciDoktoriRemoteService,
                          NotificationClient notificationClient) {
        this.pregledRepository = pregledRepository;
        this.terminRepository = terminRepository;
        this.korisniciDoktoriRemoteService = korisniciDoktoriRemoteService;
        this.notificationClient = notificationClient;
    }

    private PregledDTO toDto(Pregled pregled) {
        if (pregled == null) return null;

        String pacijentIme = "N/A Pacijent";
        String doktorIme = "N/A Doktor";

        // Dohvati ime pacijenta
        try {
            if (pregled.getPacijentId() != null) {
                Optional<PacijentDTO> pacijentDetalji = korisniciDoktoriRemoteService.getPacijentById(pregled.getPacijentId());
                if (pacijentDetalji.isPresent()) {
                    PacijentDTO pacijent = pacijentDetalji.get();
                    if (pacijent.getIme() != null && pacijent.getPrezime() != null) {
                        pacijentIme = pacijent.getIme() + " " + pacijent.getPrezime();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Greška prilikom dohvaćanja imena pacijenta: " + e.getMessage());
        }

        // Dohvati ime doktora
        try {
            if (pregled.getDoktorId() != null) {
                Optional<DoktorDTO> doktorDetalji = korisniciDoktoriRemoteService.getDoktorById(pregled.getDoktorId());
                if (doktorDetalji.isPresent()) {
                    DoktorDTO doktor = doktorDetalji.get();
                    if (doktor.getIme() != null && doktor.getPrezime() != null) {
                        doktorIme = doktor.getIme() + " " + doktor.getPrezime();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Greška prilikom dohvaćanja imena doktora: " + e.getMessage());
        }

        Double stvarnaOcjena = null;
        try {
            stvarnaOcjena = korisniciDoktoriRemoteService.getOcjenaZaPregled(pregled.getPregledID())
                    .map(o -> o.getOcjena())
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Greška pri dohvaćanju ocjene za pregled ID " + pregled.getPregledID() + ": " + e.getMessage());
        }


        return PregledDTO.builder()
                .pregledID(pregled.getPregledID())
                .pacijentID(pregled.getPacijentId())
                .doktorID(pregled.getDoktorId())
                .datumPregleda(pregled.getDatumPregleda() != null ? pregled.getDatumPregleda().toString() : null)
                .vrijemePregleda(pregled.getVrijemePregleda() != null ? pregled.getVrijemePregleda().toString() : null)
                .status(pregled.getStatus() != null ? pregled.getStatus().name() : null)
                .komentarPacijenta(pregled.getKomentarPacijenta())
                .ocjenaDoktora(stvarnaOcjena) // ✅ Prava ocjena
                .terminID(pregled.getTermin() != null ? pregled.getTermin().getTerminID() : null)
                .pacijentIme(pacijentIme)
                .doktorIme(doktorIme)
                .build();
    }


    private Pregled toEntity(PregledDTO pregledDTO) {
        if (pregledDTO == null) {
            return null;
        }
        Pregled pregled = new Pregled();
        pregled.setPacijentId(pregledDTO.getPacijentID());
        pregled.setDoktorId(pregledDTO.getDoktorID());
        pregled.setStatus(Pregled.Status.zakazan); // Default status
        pregled.setKomentarPacijenta(pregledDTO.getKomentarPacijenta());
        pregled.setOcjenaDoktora(null); // Eksplicitno postavite ocjenu na null za novi pregled

        // Parsiranje datuma i vremena iz Stringa (DTO) u LocalDate i LocalTime (entitet)
        try {
            if (pregledDTO.getDatumPregleda() != null) {
                pregled.setDatumPregleda(LocalDate.parse(pregledDTO.getDatumPregleda())); // RADI SADA: DTO.getDatumPregleda() je String
            }
        } catch (DateTimeParseException e) {
            System.err.println("Greška prilikom parsiranja datuma pregleda iz DTO: " + pregledDTO.getDatumPregleda() + " - " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neispravan format datuma pregleda. Očekivani format YYYY-MM-DD.");
        }
        try {
            if (pregledDTO.getVrijemePregleda() != null) {
                pregled.setVrijemePregleda(LocalTime.parse(pregledDTO.getVrijemePregleda())); // RADI SADA: DTO.getVrijemePregleda() je String
            }
        } catch (DateTimeParseException e) {
            System.err.println("Greška prilikom parsiranja vremena pregleda iz DTO: " + pregledDTO.getVrijemePregleda() + " - " + e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Neispravan format vremena pregleda. Očekivani format HH:MM:SS.");
        }

        return pregled;
    }

    @Transactional
    public List<PregledDTO> getAllPregledi() {
        return pregledRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PregledDTO createPregledAndPublishEvent(PregledDTO pregledDTO) {
        System.out.println("DEBUG (createPregledAndPublishEvent): Primljeni DTO - DoktorID: " + pregledDTO.getDoktorID() + ", TerminID: " + pregledDTO.getTerminID());

        Termin termin = terminRepository.findById(pregledDTO.getTerminID())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Termin sa ID-em " + pregledDTO.getTerminID() + " nije pronađen."));

        if (termin.getStatusTermina() == StatusTermina.ZAKAZAN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Odabrani termin je već zauzet.");
        }

        if (!pregledDTO.getDoktorID().equals(termin.getDoktorID())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DoktorID u pregledu (" + pregledDTO.getDoktorID() + ") ne odgovara doktoru dodijeljenom terminu (" + termin.getDoktorID() + ").");
        }

        Pregled pregled = toEntity(pregledDTO); // toEntity sada pravilno parsira
        pregled.setTermin(termin);
        // Direktno postavite LocalDate i LocalTime iz Termina (koji bi već trebao biti LocalDate/LocalTime)
        pregled.setDatumPregleda(termin.getDatum());
        pregled.setVrijemePregleda(termin.getVrijeme());
        pregled.setStatus(Pregled.Status.zakazan);
        pregled.setOcjenaDoktora(null);

        Pregled savedPregled = pregledRepository.save(pregled);

        termin.setStatusTermina(StatusTermina.ZAKAZAN);
        terminRepository.save(termin);

        // Dohvaćanje imena pacijenta za poruku obavijesti
        String pacijentIme = getKorisnikIme(pregledDTO.getPacijentID(), "PACIJENT");

        // Slanje notifikacije doktoru
        try {
            String notificationMessage = String.format("Novi pregled zakazan za pacijenta %s, %s u %s.",
                    pacijentIme,
                    savedPregled.getDatumPregleda().toString(), // Koristi LocalDate.toString()
                    savedPregled.getVrijemePregleda().toString() // Koristi LocalTime.toString()
            );
            notificationClient.sendNotification(
                    (long) savedPregled.getDoktorId(),
                    notificationMessage,
                    "DOKTOR"
            );
            System.out.println("Notifikacija uspješno poslana doktoru sa ID-om: " + savedPregled.getDoktorId());
        } catch (Exception e) {
            System.err.println("Greška pri slanju notifikacije doktoru sa ID-om " + savedPregled.getDoktorId() + ": " + e.getMessage());
        }

        String correlationId = UUID.randomUUID().toString();

        return toDto(savedPregled);
    }

    // Metoda za dohvaćanje imena korisnika (pacijenta/doktora)
    private String getKorisnikIme(Integer id, String tip) {
        if (id == null) return "N/A";
        if ("PACIJENT".equals(tip)) {
            try {
                Optional<PacijentDTO> pacijent = korisniciDoktoriRemoteService.getPacijentById(id);
                return pacijent.map(p -> p.getIme() + " " + p.getPrezime()).orElse("N/A Pacijent");
            } catch (Exception e) {
                System.err.println("Greška u getKorisnikIme za Pacijenta ID " + id + ": " + e.getMessage());
                return "N/A Pacijent";
            }
        } else if ("DOKTOR".equals(tip)) {
            try {
                Optional<DoktorDTO> doktor = korisniciDoktoriRemoteService.getDoktorById(id);
                return doktor.map(d -> d.getIme() + " " + d.getPrezime()).orElse("N/A Doktor");
            } catch (Exception e) {
                System.err.println("Greška u getKorisnikIme za Doktora ID " + id + ": " + e.getMessage());
                return "N/A Doktor";
            }
        }
        return "N/A";
    }

    @Transactional
    public void compensatePregledCreation(Integer pregledId, Integer terminId, String correlationId) {
        System.out.println("SAGA COMPENSATION: Primljen zahtjev za kompenzaciju za Pregled ID: " + pregledId + ", Termin ID: " + terminId + ", Correlation ID: " + correlationId);

        Optional<Pregled> pregledOptional = pregledRepository.findById(pregledId);
        if (pregledOptional.isPresent()) {
            Pregled pregled = pregledOptional.get();
            pregledRepository.delete(pregled);
            System.out.println("SAGA COMPENSATION: Obrisan Pregled ID: " + pregledId + " kao kompenzacija.");
        } else {
            System.err.println("SAGA COMPENSATION: Pregled sa ID-em " + pregledId + " nije pronađen za kompenzaciju (možda već obrisan ili nikad kreiran).");
        }

        Optional<Termin> terminOptional = terminRepository.findById(terminId);
        if (terminOptional.isPresent()) {
            Termin termin = terminOptional.get();
            if (termin.getStatusTermina() == StatusTermina.ZAKAZAN) {
                termin.setStatusTermina(StatusTermina.DOSTUPAN);
                terminRepository.save(termin);
                System.out.println("SAGA COMPENSATION: Resetovan status Termina ID: " + terminId + " na DOSTUPAN kao kompenzacija.");
            }
        } else {
            System.err.println("SAGA COMPENSATION: Termin sa ID-em " + terminId + " nije pronađen za kompenzaciju.");
        }
    }

    @Transactional
    public void finalizeAppointmentCreation(Integer appointmentId, String correlationId) {
        System.out.println("SAGA FINALIZATION: Primljen zahtjev za finalizaciju za Pregled ID: " + appointmentId + ", Correlation ID: " + correlationId);
        System.out.println("SAGA FINALIZATION: Pregled ID " + appointmentId + " je uspješno obrađen asinkrono.");
    }


    public Optional<PregledDTO> getPregledById(Integer pregledID) {
        return pregledRepository.findById(pregledID)
                .map(this::toDto);
    }

    public List<PregledDTO> getPreglediByStatus(Pregled.Status status) {
        return pregledRepository.findByStatus(status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<PregledDTO> updatePregled(Integer pregledID, PregledDTO updatedPregledDTO) {
        return Optional.ofNullable(pregledRepository.findById(pregledID)
                .map(existingPregled -> {
                    System.out.println("DEBUG (updatePregled): Primljeni DTO - Pregled ID: " + pregledID + ", DoktorID: " + updatedPregledDTO.getDoktorID() + ", TerminID: " + updatedPregledDTO.getTerminID() + ", Ocjena: " + updatedPregledDTO.getOcjenaDoktora());

                    Termin oldTermin = existingPregled.getTermin();

                    Termin newTermin = terminRepository.findById(updatedPregledDTO.getTerminID())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Novi termin s ID-em " + updatedPregledDTO.getTerminID() + " nije pronađen."));

                    System.out.println("DEBUG (updatePregled): Dohvaćeni Novi Termin - ID: " + newTermin.getTerminID() + ", DoktorID: " + newTermin.getDoktorID() + ", Datum: " + newTermin.getDatum() + ", Vrijeme: " + newTermin.getVrijeme());

                    if (!updatedPregledDTO.getDoktorID().equals(newTermin.getDoktorID())) {
                        System.err.println("ERROR (updatePregled): Doktor ID mismatch! DTO Doktor ID: " + updatedPregledDTO.getDoktorID() + ", Termin Doktor ID: " + newTermin.getDoktorID());
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "DoktorID u ažuriranom pregledu (" + updatedPregledDTO.getDoktorID() + ") ne odgovara doktoru dodijeljenom novom terminu (" + newTermin.getDoktorID() + ").");
                    }

                    if (oldTermin != null && !oldTermin.getTerminID().equals(newTermin.getTerminID())) {
                        oldTermin.setStatusTermina(StatusTermina.DOSTUPAN);
                        terminRepository.save(oldTermin);
                        System.out.println("DEBUG (updatePregled): Stari termin ID " + oldTermin.getTerminID() + " oslobođen (postavljen na DOSTUPAN).");
                    }

                    existingPregled.setPacijentId(updatedPregledDTO.getPacijentID());
                    existingPregled.setDoktorId(updatedPregledDTO.getDoktorID());
                    existingPregled.setTermin(newTermin);
                    // Direktno postavite LocalDate i LocalTime iz newTermin
                    existingPregled.setDatumPregleda(newTermin.getDatum());
                    existingPregled.setVrijemePregleda(newTermin.getVrijeme());

                    Pregled.Status newStatus = updatedPregledDTO.getStatus() != null ? Pregled.Status.valueOf(updatedPregledDTO.getStatus()) : existingPregled.getStatus();
                    existingPregled.setStatus(newStatus);
                    existingPregled.setKomentarPacijenta(updatedPregledDTO.getKomentarPacijenta());
                    existingPregled.setOcjenaDoktora(updatedPregledDTO.getOcjenaDoktora());

                    if (newStatus == Pregled.Status.obavljen || newStatus == Pregled.Status.otkazan) {
                        newTermin.setStatusTermina(StatusTermina.DOSTUPAN);
                        System.out.println("DEBUG (updatePregled): Novi termin ID " + newTermin.getTerminID() + " postavljen na DOSTUPAN (pregled obavljen/otkazan).");
                    } else if (newStatus == Pregled.Status.zakazan) {
                        newTermin.setStatusTermina(StatusTermina.ZAKAZAN);
                        System.out.println("DEBUG (updatePregled): Novi termin ID " + newTermin.getTerminID() + " postavljen na ZAKAZAN (pregled zakazan/u toku).");
                    }
                    terminRepository.save(newTermin);

                    Pregled savedPregled = pregledRepository.save(existingPregled);

                    return toDto(savedPregled);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregled s ID-em " + pregledID + " nije pronađen za ažuriranje.")));
    }

    @Transactional
    public boolean deletePregled(Integer pregledID) {
        Optional<Pregled> pregledOptional = pregledRepository.findById(pregledID);
        if (pregledOptional.isPresent()) {
            Pregled pregled = pregledOptional.get();
            if (pregled.getTermin() != null) {
                Termin termin = pregled.getTermin();
                termin.setStatusTermina(StatusTermina.DOSTUPAN);
                terminRepository.save(termin);
                System.out.println("DEBUG (deletePregled): Termin ID " + termin.getTerminID() + " oslobođen (postavljen na DOSTUPAN) jer je pregled obrisan.");
            }
            pregledRepository.deleteById(pregledID);
            System.out.println("DEBUG (deletePregled): Pregled ID " + pregledID + " obrisan.");
            if (pregled.getDoktorId() != null) {
                korisniciDoktoriRemoteService.triggerDoktorRatingRecalculation(pregled.getDoktorId());
            }
            return true;
        }
        return false;
    }

    public List<PregledDTO> getPreglediForDoktor(Integer doktorID) {
        return pregledRepository.findByDoktorId(doktorID).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PregledDTO> getZakazaniPreglediForPacijent(Integer pacijentID) {
        return pregledRepository.findByPacijentIdAndStatus(pacijentID, Pregled.Status.zakazan).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Dohvaća sve preglede za određenog pacijenta, bez obzira na status.
     * @param pacijentID ID pacijenta.
     * @return Lista svih pregleda za pacijenta.
     */
    @Transactional(readOnly = true)
    public List<PregledDTO> getPreglediForPacijent(Integer pacijentID) {
        return pregledRepository.findByPacijentId(pacijentID).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<PregledDTO> getPreglediForDoktorByStatus(Integer doktorID, Pregled.Status status) {
        return pregledRepository.findByDoktorIdAndStatus(doktorID, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ISPRAVLJENO: Potpis metode sada prihvata LocalDate
    public List<PregledDTO> getPreglediByDate(LocalDate datum) {
        return pregledRepository.findByDatumPregleda(datum).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ISPRAVLJENO: Potpis metode sada prihvata LocalDate
    public List<PregledDTO> getPreglediInDateRange(LocalDate startDate, LocalDate endDate, Pregled.Status status) {
        return pregledRepository.findByDatumPregledaBetweenAndStatus(startDate, endDate, status).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public long countPreglediByDoktor(Integer doktorID) {
        return pregledRepository.countByDoktorId(doktorID);
    }
}
