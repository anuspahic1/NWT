package com.medicinska.rezervacija.termini_pregledi.repository;

import com.medicinska.rezervacija.termini_pregledi.model.Pregled;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // Dodano
import java.util.List;

@Repository
public interface PregledRepository extends JpaRepository<Pregled, Integer> {
    List<Pregled> findByDoktorId(Integer doktorId);
    List<Pregled> findByPacijentIdAndStatus(Integer pacijentId, Pregled.Status status);
    List<Pregled> findByPacijentId(Integer pacijentId);
    List<Pregled> findByDoktorIdAndStatus(Integer doktorId, Pregled.Status status);
    long countByDoktorId(Integer doktorId);
    List<Pregled> findByStatus(Pregled.Status status);

    // ISPRAVLJENO: Potpis metode sada prihvata LocalDate
    List<Pregled> findByDatumPregleda(LocalDate datumPregleda);

    // ISPRAVLJENO: Potpisi metoda sada prihvataju LocalDate
    List<Pregled> findByDatumPregledaBetweenAndStatus(LocalDate startDate, LocalDate endDate, Pregled.Status status);
}
