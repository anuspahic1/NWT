package com.medicinska.rezervacija.termini_pregledi.config;

import com.medicinska.rezervacija.termini_pregledi.model.Termin;
import com.medicinska.rezervacija.termini_pregledi.model.Pregled;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import com.medicinska.rezervacija.termini_pregledi.repository.TerminRepository;
import com.medicinska.rezervacija.termini_pregledi.repository.PregledRepository;
import com.medicinska.rezervacija.termini_pregledi.dto.DoktorDTO;
import com.medicinska.rezervacija.termini_pregledi.dto.PacijentDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
public class DataLoader {

    private static final String KORISNICI_DOKTORI_SERVICE_ID_BASE_PATH = "http://KORISNICI-DOKTORI";
    private static final String KORISNICI_DOKTORI_INTERNAL_API_PATH_PREFIX = "/korisnici-doktori/api";


    @Bean
    CommandLineRunner initTerminiPreglediDatabase(
            TerminRepository terminRepository,
            PregledRepository pregledRepository,
            RestTemplate restTemplate
    ) {
        return args -> {
            System.out.println("Pokrećem DataLoader za Termini-Pregledi servis...");

            if (terminRepository.count() > 0 || pregledRepository.count() > 0) {
                System.out.println("Termini ili pregledi već postoje, preskačem inicijalizaciju DataLoader-a.");
                return;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Integer doktorId1 = null;
            Integer doktorId2 = null;
            Integer pacijentId1 = null;
            Integer pacijentId2 = null;

            try {
                String doktoriUrl = KORISNICI_DOKTORI_SERVICE_ID_BASE_PATH + KORISNICI_DOKTORI_INTERNAL_API_PATH_PREFIX + "/doktori";
                System.out.println("[DataLoader] Pokušavam dohvatiti doktore iz: " + doktoriUrl);
                ResponseEntity<List<DoktorDTO>> doktoriResponse = restTemplate.exchange(
                        doktoriUrl,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<DoktorDTO>>() {}
                );

                List<DoktorDTO> doktori = doktoriResponse.getBody();
                if (doktori != null && !doktori.isEmpty()) {
                    Optional<DoktorDTO> doctorUser = doktori.stream()
                            .filter(d -> "Doctor".equals(d.getIme()) && "User".equals(d.getPrezime()))
                            .findFirst();
                    if (doctorUser.isPresent()) {
                        doktorId1 = doctorUser.get().getDoktorID();
                        System.out.println("[DataLoader] Pronađen doktorId1 (Doctor User): " + doktorId1);
                    } else if (doktori.size() > 0) {
                        doktorId1 = doktori.get(0).getDoktorID();
                        System.out.println("[DataLoader] Pronađen doktorId1 (prvi dostupni doktor): " + doktorId1);
                    }

                    Optional<DoktorDTO> huseinHasanovic = doktori.stream()
                            .filter(d -> "Husein".equals(d.getIme()) && "Hasanović".equals(d.getPrezime()))
                            .findFirst();
                    if (huseinHasanovic.isPresent()) {
                        doktorId2 = huseinHasanovic.get().getDoktorID();
                        System.out.println("[DataLoader] Pronađen doktorId2 (Husein Hasanović): " + doktorId2);
                    } else if (doktori.size() > 1) {
                        doktorId2 = doktori.get(1).getDoktorID();
                        System.out.println("[DataLoader] Pronađen doktorId2 (drugi dostupni doktor): " + doktorId2);
                    }
                }

                String pacijentiUrl = KORISNICI_DOKTORI_SERVICE_ID_BASE_PATH + KORISNICI_DOKTORI_INTERNAL_API_PATH_PREFIX + "/pacijenti";
                System.out.println("[DataLoader] Pokušavam dohvatiti pacijente iz: " + pacijentiUrl);
                ResponseEntity<List<PacijentDTO>> pacijentiResponse = restTemplate.exchange(
                        pacijentiUrl,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<List<PacijentDTO>>() {}
                );

                List<PacijentDTO> pacijenti = pacijentiResponse.getBody();
                if (pacijenti != null && !pacijenti.isEmpty()) {
                    Optional<PacijentDTO> patientUser = pacijenti.stream()
                            .filter(p -> "Patient".equals(p.getIme()) && "User".equals(p.getPrezime()))
                            .findFirst();
                    if (patientUser.isPresent()) {
                        pacijentId1 = patientUser.get().getPacijentID();
                        System.out.println("[DataLoader] Pronađen pacijentId1 (Patient User): " + pacijentId1);
                    } else if (pacijenti.size() > 0) {
                        pacijentId1 = pacijenti.get(0).getPacijentID();
                        System.out.println("[DataLoader] Pronađen pacijentId1 (prvi dostupni pacijent): " + pacijentId1);
                    }

                    Optional<PacijentDTO> dzenanaSelimovic = pacijenti.stream()
                            .filter(p -> "Dženana".equals(p.getIme()) && "Selimović".equals(p.getPrezime()))
                            .findFirst();
                    if (dzenanaSelimovic.isPresent()) {
                        pacijentId2 = dzenanaSelimovic.get().getPacijentID();
                        System.out.println("[DataLoader] Pronađen pacijentId2 (Dženana Selimović): " + pacijentId2);
                    } else if (pacijenti.size() > 1) {
                        pacijentId2 = pacijenti.get(1).getPacijentID();
                        System.out.println("[DataLoader] Pronađen pacijentId2 (drugi dostupni pacijent): " + pacijentId2);
                    }
                }

                if (doktorId1 == null || pacijentId1 == null) {
                    System.err.println("[DataLoader] Nisu pronađeni svi potrebni ID-evi doktora/pacijenata. Provjerite da li su Korisnici-Doktori servis pokrenut i da li ima default korisnike.");
                    return;
                }

            } catch (Exception e) {
                System.err.println("[DataLoader] Greška prilikom dohvaćanja doktora/pacijenata iz Korisnici-Doktori servisa: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            System.out.println("[DataLoader] Kreiram termine...");

            Termin termin1 = Termin.builder()
                    .doktorID(doktorId1)
                    .datum(Date.valueOf("2025-07-15").toLocalDate())
                    .vrijeme(Time.valueOf("10:00:00").toLocalTime())
                    .statusTermina(StatusTermina.DOSTUPAN)
                    .build();
            termin1 = terminRepository.save(termin1);
            System.out.println("[DataLoader] Kreiran termin 1: ID=" + termin1.getTerminID() + ", DoktorID=" + termin1.getDoktorID());

            Termin termin2 = Termin.builder()
                    .doktorID(doktorId1)
                    .datum(Date.valueOf("2025-07-15").toLocalDate())
                    .vrijeme(Time.valueOf("11:00:00").toLocalTime())
                    .statusTermina(StatusTermina.DOSTUPAN)
                    .build();
            termin2 = terminRepository.save(termin2);
            System.out.println("[DataLoader] Kreiran termin 2: ID=" + termin2.getTerminID() + ", DoktorID=" + termin2.getDoktorID());

            Termin termin3 = null;
            if (doktorId2 != null) {
                termin3 = Termin.builder()
                        .doktorID(doktorId2)
                        .datum(Date.valueOf("2025-07-16").toLocalDate())
                        .vrijeme(Time.valueOf("09:30:00").toLocalTime())
                        .statusTermina(StatusTermina.DOSTUPAN) //
                        .build();
                termin3 = terminRepository.save(termin3);
                System.out.println("[DataLoader] Kreiran termin 3: ID=" + termin3.getTerminID() + ", DoktorID=" + termin3.getDoktorID());
            } else {
                System.out.println("[DataLoader] Nije moguće kreirati termin 3, doktorId2 nije pronađen.");
            }

            System.out.println("[DataLoader] Kreiram preglede...");

            Pregled pregled1 = Pregled.builder()
                    .pacijentId(pacijentId1)
                    .doktorId(doktorId1)
                    .termin(termin1)
                    .datumPregleda(Date.valueOf(termin1.getDatum()).toLocalDate())
                    .vrijemePregleda(Time.valueOf(termin1.getVrijeme()).toLocalTime())
                    .status(Pregled.Status.zakazan)
                    .komentarPacijenta("Standardni pregled.")
                    .build();
            pregled1 = pregledRepository.save(pregled1);
            System.out.println("[DataLoader] Kreiran pregled 1: ID=" + pregled1.getPregledID());

            termin1.setStatusTermina(StatusTermina.ZAKAZAN);
            terminRepository.save(termin1);
            System.out.println("[DataLoader] Termin 1 ažuriran: Status = " + termin1.getStatusTermina());

            Pregled pregled2 = null;
            if (pacijentId2 != null) {
                pregled2 = Pregled.builder()
                        .pacijentId(pacijentId2)
                        .doktorId(doktorId1)
                        .termin(termin2)
                        .datumPregleda(Date.valueOf(termin2.getDatum()).toLocalDate())
                        .vrijemePregleda(Time.valueOf(termin2.getVrijeme()).toLocalTime())
                        .status(Pregled.Status.obavljen)
                        .ocjenaDoktora(4.0)
                        .komentarPacijenta("Doktor je bio vrlo profesionalan.")
                        .build();
                pregled2 = pregledRepository.save(pregled2);
                System.out.println("[DataLoader] Kreiran pregled 2: ID=" + pregled2.getPregledID());

                termin2.setStatusTermina(StatusTermina.DOSTUPAN);
                terminRepository.save(termin2);
                System.out.println("[DataLoader] Termin 2 ažuriran: Status = " + termin2.getStatusTermina());
            } else {
                System.out.println("[DataLoader] Nije moguće kreirati pregled 2, pacijentId2 nije pronađen.");
            }

            System.out.println("[DataLoader] Završena inicijalizacija baze podataka za Termini-Pregledi servis.");
        };
    }
}
