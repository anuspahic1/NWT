package com.medicinska.rezervacija.korisnici_doktori.repository;

import com.medicinska.rezervacija.korisnici_doktori.model.Specijalizacija;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpecijalizacijaRepository extends JpaRepository<Specijalizacija, Integer> {

    List<Specijalizacija> findByNazivSpecijalizacijeContainingIgnoreCase(String naziv);

    @EntityGraph(attributePaths = {"doktorSpecijalizacije", "doktorSpecijalizacije.doktor"})
    @Override
    List<Specijalizacija> findAll();

    @EntityGraph(attributePaths = {"doktorSpecijalizacije", "doktorSpecijalizacije.doktor"})
    Optional<Specijalizacija> findByNazivSpecijalizacije(String nazivSpecijalizacije);

    @EntityGraph(attributePaths = {"doktorSpecijalizacije", "doktorSpecijalizacije.doktor"})
    Optional<Specijalizacija> findById(Integer id);
}