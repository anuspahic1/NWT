package com.medicinska.rezervacija.korisnici_doktori.repository;

import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PacijentRepository extends JpaRepository<Pacijent, Integer> {

    List<Pacijent> findByImeContainingIgnoreCaseOrPrezimeContainingIgnoreCase(String ime, String prezime);
    @Query("SELECT p FROM Pacijent p WHERE SIZE(p.ocjene) > :minOcjena")
    List<Pacijent> findActivePatients(@Param("minOcjena") int minOcjena);

    Optional<Pacijent> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"ocjene", "ocjene.doktor"})
    List<Pacijent> findAll();
    Optional<Pacijent> findByEmail(String email);



}

