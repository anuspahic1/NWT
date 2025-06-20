package com.medicinska.rezervacija.korisnici_doktori.repository;

import com.medicinska.rezervacija.korisnici_doktori.model.OcjenaDoktora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Dodan import ako se koristi Optional

@Repository
public interface OcjenaDoktoraRepository extends JpaRepository<OcjenaDoktora, Integer> {
    List<OcjenaDoktora> findByDoktor_DoktorID(Integer doktorId);

    // Ova metoda ostaje, ali se više ne koriste filteri za komentar
    List<OcjenaDoktora> findByOcjenaBetween(Double minOcjena, Double maxOcjena);

    // UKLONJENO: Više nam ne trebaju metode za filtriranje po komentaru
    // List<OcjenaDoktora> findByOcjenaBetweenAndKomentarIsNotNull(Double minOcjena, Double maxOcjena);
    // List<OcjenaDoktora> findByOcjenaBetweenAndKomentarIsNull(Double minOcjena, Double maxOcjena);

    // KLJUČNA IZMJENA: Metoda za provjeru da li postoji ocjena za određeni pregled od određenog pacijenta
    boolean existsByPacijent_PacijentIDAndPregledID(Integer pacijentID, Integer pregledID);

    // Ako trebate dohvatit specifičnu ocjenu za pregled, možete dodati:
    Optional<OcjenaDoktora> findByPregledID(Integer pregledID);
}
