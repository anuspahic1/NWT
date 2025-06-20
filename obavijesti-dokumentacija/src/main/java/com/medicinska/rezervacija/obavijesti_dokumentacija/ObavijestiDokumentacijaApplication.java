package com.medicinska.rezervacija.obavijesti_dokumentacija;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Poruka;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Racun;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.RacunStatus;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DokumentacijaRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.NotifikacijaRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PorukaRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.RacunRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PregledDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija.Uloga;

@SpringBootApplication
@EnableFeignClients
public class ObavijestiDokumentacijaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ObavijestiDokumentacijaApplication.class, args);
	}



	@Bean
	@LoadBalanced
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	private Date convertToDateViaInstant(LocalDate dateToConvert) {
		return java.util.Date.from(dateToConvert.atStartOfDay()
				.atZone(ZoneId.systemDefault())
				.toInstant());
	}

	@Bean
	public CommandLineRunner initDatabase(
			DokumentacijaRepository dokumentacijaRepository,
			NotifikacijaRepository notificationRepository,
			PorukaRepository porukaRepository,
			RacunRepository racunRepository,
			RestTemplate restTemplate) {
		return args -> {
			System.out.println("Pokrećem DataLoader za Obavijesti-Dokumentacija servis...");

			String KORISNICI_DOKTORI_BASE_URL_FOR_INTERNAL_CALLS = "http://KORISNICI-DOKTORI/korisnici-doktori/api";
			String TERMINI_PREGLEDI_BASE_URL_FOR_INTERNAL_CALLS = "http://TERMINI-PREGLEDI/termini-pregledi/api";


			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			HttpEntity<String> entity = new HttpEntity<>(headers);

			Long pacijentId1 = null;
			Long pacijentId2 = null;
			Long doktorId1 = null;
			Long doktorId2 = null;
			Integer pregledId1Integer = null;
			Integer pregledId2Integer = null;


			try {
				ResponseEntity<List<PacijentDTO>> pacijentiResponse = restTemplate.exchange(
						KORISNICI_DOKTORI_BASE_URL_FOR_INTERNAL_CALLS + "/pacijenti",
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<List<PacijentDTO>>() {}
				);
				List<PacijentDTO> pacijenti = pacijentiResponse.getBody();
				if (pacijenti != null && pacijenti.size() >= 2) {
					pacijentId1 = Long.valueOf(pacijenti.get(0).getPacijentID());
					pacijentId2 = Long.valueOf(pacijenti.get(1).getPacijentID());
					System.out.println("[DataLoader Obavijesti] Pronađen pacijentId1: " + pacijentId1 + ", pacijentId2: " + pacijentId2);
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno pacijenata u Korisnici-Doktori servisu za inicijalizaciju.");
				}

				ResponseEntity<List<DoktorDTO>> doktoriResponse = restTemplate.exchange(
						KORISNICI_DOKTORI_BASE_URL_FOR_INTERNAL_CALLS + "/doktori",
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<List<DoktorDTO>>() {}
				);
				List<DoktorDTO> doktori = doktoriResponse.getBody();
				if (doktori != null && doktori.size() >= 2) {
					doktorId1 = Long.valueOf(doktori.get(0).getDoktorID());
					doktorId2 = Long.valueOf(doktori.get(1).getDoktorID());
					System.out.println("[DataLoader Obavijesti] Pronađen doktorId1: " + doktorId1 + ", doktorId2: " + doktorId2);
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno doktora u Korisnici-Doktori servisu za inicijalizaciju.");
				}

				ResponseEntity<List<PregledDTO>> preglediResponse = restTemplate.exchange(
						TERMINI_PREGLEDI_BASE_URL_FOR_INTERNAL_CALLS + "/pregledi",
						HttpMethod.GET,
						entity,
						new ParameterizedTypeReference<List<PregledDTO>>() {}
				);
				List<PregledDTO> pregledi = preglediResponse.getBody();
				if (pregledi != null && pregledi.size() >= 2) {
					pregledId1Integer = pregledi.get(0).getPregledID();
					pregledId2Integer = pregledi.get(1).getPregledID();
					System.out.println("[DataLoader Obavijesti] Pronađen pregledId1 (Integer): " + pregledId1Integer + ", pregledId2 (Integer): " + pregledId2Integer);
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno pregleda u Termini-Pregledi servisu za inicijalizaciju.");
				}

			} catch (Exception e) {
				System.err.println("[DataLoader Obavijesti] Greška prilikom dohvaćanja ID-jeva iz drugih servisa: " + e.getMessage());
				e.printStackTrace();
				return;
			}

			if (dokumentacijaRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka 'obavijesti-dokumentacija' sa početnim podacima (dokumentacija)...");

				System.out.println("Dokumentacija popunjavanje završeno.");
			} else {
				System.out.println("Dokumentacija već postoji, preskačem inicijalizaciju.");
			}

			if (notificationRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka 'obavijesti-dokumentacija' sa početnim podacima (notifikacije)...");

				if (pacijentId1 != null) {
					Notifikacija notif1 = Notifikacija.builder()
							.korisnikID(pacijentId1)
							.uloga(Uloga.PACIJENT)
							.sadrzaj("Vaš pregled kod Dr. Petrovića je zakazan za 15. juni 2024.")
							.status(Notifikacija.StatusNotifikacije.POSLANO)
							.datumSlanja(LocalDateTime.now().minusDays(2))
							.build();
					notificationRepository.save(notif1);
					System.out.println("Notifikacija 1 uspješno popunjena.");
				}

				if (doktorId1 != null) {
					Notifikacija notif2 = Notifikacija.builder()
							.korisnikID(doktorId1)
							.uloga(Uloga.DOKTOR)
							.sadrzaj("Imate novi zahtjev za pregled od pacijenta Adna K.")
							.status(Notifikacija.StatusNotifikacije.POSLANO)
							.datumSlanja(LocalDateTime.now().minusHours(5))
							.build();
					notificationRepository.save(notif2);
					System.out.println("Notifikacija 2 uspješno popunjena.");
				}

				if (pacijentId1 != null) {
					Notifikacija notif3 = Notifikacija.builder()
							.korisnikID(pacijentId1)
							.uloga(Uloga.PACIJENT)
							.sadrzaj("Vaši laboratorijski nalazi su dostupni u sekciji Dokumenti.")
							.status(Notifikacija.StatusNotifikacije.POSLANO)
							.datumSlanja(LocalDateTime.now().minusHours(1))
							.build();
					notificationRepository.save(notif3);
					System.out.println("Notifikacija 3 uspješno popunjena.");
				}

				System.out.println("Notifikacije popunjavanje završeno.");

			} else {
				System.out.println("Notifikacije već postoje, preskačem inicijalizaciju.");
			}

			if (porukaRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka 'obavijesti-dokumentacija' sa početnim podacima (poruke)...");
				if (pacijentId1 != null && doktorId1 != null) {
					Poruka poruka1 = Poruka.builder()
							.senderId(pacijentId1)
							.senderType(Uloga.PACIJENT)
							.receiverId(doktorId1)
							.receiverType(Uloga.DOKTOR)
							.subject("Pitanje o nalazima")
							.content("Poštovani doktore, imam pitanje u vezi s mojim nedavnim laboratorijskim nalazima.")
							.timestamp(LocalDateTime.now().minusDays(1))
							.build();
					porukaRepository.save(poruka1);
					System.out.println("Poruka 1 uspješno popunjena.");
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno ID-jeva za popunjavanje Poruke 1.");
				}
				System.out.println("Poruke popunjavanje završeno.");
			} else {
				System.out.println("Poruke već postoje, preskačem inicijalizaciju.");
			}


			if (racunRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka 'obavijesti-dokumentacija' sa početnim podacima (računi)...");
				if (pacijentId1 != null && doktorId1 != null) {
					Racun racun1 = Racun.builder()
							.pacijentID(pacijentId1)
							.doktorID(doktorId1)
							.datumIzdavanja(LocalDate.of(2024, 5, 20))
							.iznos(75.50)
							.status(RacunStatus.NEPLACEN)
							.opis("Pregled i konzultacije")
							.build();
					racunRepository.save(racun1);
					System.out.println("Račun 1 uspješno popunjen.");
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno ID-jeva za popunjavanje Računa 1.");
				}

				if (pacijentId2 != null && doktorId2 != null) {
					Racun racun2 = Racun.builder()
							.pacijentID(pacijentId2)
							.doktorID(doktorId2)
							.datumIzdavanja(LocalDate.of(2024, 6, 1))
							.iznos(120.00)
							.status(RacunStatus.PLACEN)
							.opis("Laboratorijske pretrage")
							.build();
					racunRepository.save(racun2);
					System.out.println("Račun 2 uspješno popunjen.");
				} else {
					System.err.println("[DataLoader Obavijesti] Nema dovoljno ID-jeva za popunjavanje Računa 2.");
				}

				System.out.println("Računi popunjavanje završeno.");
			} else {
				System.out.println("Računi već postoje, preskačem inicijalizaciju.");
			}
		};
	}
}
