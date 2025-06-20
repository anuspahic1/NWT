package com.medicinska.rezervacija.korisnici_doktori;

import com.medicinska.rezervacija.korisnici_doktori.model.*;
import com.medicinska.rezervacija.korisnici_doktori.repository.*;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@SpringBootApplication
@EnableDiscoveryClient
public class KorisniciDoktoriApplication {

	public static void main(String[] args) {
		SpringApplication.run(KorisniciDoktoriApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner initDatabase(
			DoktorRepository doktorRepository,
			PacijentRepository pacijentRepository,
			SpecijalizacijaRepository specijalizacijaRepository,
			OcjenaDoktoraRepository ocjenaDoktoraRepository,
			MedicinskaHistorijaRepository medicinskaHistorijaRepository) {

		return args -> {
			if (specijalizacijaRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka Korisnici-Doktori sa početnim podacima (specijalizacije)...");
				specijalizacijaRepository.deleteAll();
				List<Specijalizacija> specijalizacije = Arrays.asList(
						new Specijalizacija(null, "Kardiolog"),
						new Specijalizacija(null, "Neurolog"),
						new Specijalizacija(null, "Ortoped"),
						new Specijalizacija(null, "Pedijatar"),
						new Specijalizacija(null, "Hirurg"),
						new Specijalizacija(null, "Dermatolog"),
						new Specijalizacija(null, "Oftamolog"),
						new Specijalizacija(null, "Psihijatar")
				);
				specijalizacijaRepository.saveAll(specijalizacije);
				System.out.println("Specijalizacije uspješno popunjene.");
			} else {
				System.out.println("Specijalizacije već postoje, preskačem inicijalizaciju.");
			}

			Optional<Pacijent> pacijent1Optional = pacijentRepository.findByEmail("patient@example.com");
			Optional<Doktor> doktor1Optional = doktorRepository.findByEmail("doctor@example.com");

			if (medicinskaHistorijaRepository.count() == 0) {
				System.out.println("Popunjavam bazu podataka Korisnici-Doktori sa početnim podacima (medicinska historija)...");

				if (pacijent1Optional.isPresent() && doktor1Optional.isPresent()) {
					Pacijent pacijent1 = pacijent1Optional.get();
					Doktor doktor1 = doktor1Optional.get();

					MedicinskaHistorija zapis1 = MedicinskaHistorija.builder()
							.pacijent(pacijent1)
							.doktor(doktor1)
							.datumZapisivanja(LocalDate.of(2023, 10, 20))
							.dijagnoza("Akutni bronhitis")
							.lijecenje("Antibiotici, mirovanje, unos tekućine.")
							.napomene("Pacijent se oporavio u roku od 7 dana.")
							.build();
					medicinskaHistorijaRepository.save(zapis1);
					System.out.println("Medicinska historija zapis 1 uspješno popunjen.");

					MedicinskaHistorija zapis2 = MedicinskaHistorija.builder()
							.pacijent(pacijent1)
							.doktor(doktor1)
							.datumZapisivanja(LocalDate.of(2024, 1, 10))
							.dijagnoza("Gripa ")
							.lijecenje("Antivirusni lijekovi, simptomatska terapija.")
							.napomene("Preporučena vakcinacija za narednu sezonu.")
							.build();
					medicinskaHistorijaRepository.save(zapis2);
					System.out.println("Medicinska historija zapis 2 uspješno popunjen.");

				} else {
					System.err.println("Nije moguće popuniti Medicinsku Historiju: Nedostaju pacijenti ili doktori u bazi.");
				}
				System.out.println("Medicinska historija popunjavanje završeno.");
			} else {
				System.out.println("Medicinska historija već postoji, preskačem inicijalizaciju.");
			}

			if (ocjenaDoktoraRepository.count() == 0 && pacijentRepository.count() > 0 && doktorRepository.count() > 0) {
				System.out.println("Popunjavam bazu podataka Korisnici-Doktori sa početnim podacima (ocjene doktora)...");
				Pacijent pacijent1 = pacijentRepository.findByEmail("patient@example.com").orElse(null);
				Doktor doktor1 = doktorRepository.findByEmail("doctor1@example.com").orElse(null);

				if (pacijent1 != null && doktor1 != null) {
					createDoctorRating(ocjenaDoktoraRepository, pacijent1, doktor1, 5.0, "Izuzetan doktor!");
					System.out.println("Ocjena doktora 1 uspješno popunjena.");
				} else {
					System.err.println("[DataLoader Korisnici-Doktori] Nije moguće popuniti ocjene doktora, pacijent ili doktor nisu pronađeni.");
				}
			} else {
				System.out.println("Ocjene doktora već postoje ili pacijenti/doktori nisu inicijalizirani, preskačem inicijalizaciju.");
			}
		};
	}

	private void createDoctorRating(OcjenaDoktoraRepository repo,
									Pacijent pacijent, Doktor doktor,
									Double ocjena, String komentar) {
		OcjenaDoktora rating = new OcjenaDoktora();
		rating.setPacijent(pacijent);
		rating.setDoktor(doktor);
		rating.setOcjena(ocjena);
		repo.save(rating);
	}
}
