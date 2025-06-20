package com.medicinska.rezervacija.termini_pregledi.repository;

import com.medicinska.rezervacija.termini_pregledi.model.Termin;
import com.medicinska.rezervacija.termini_pregledi.model.StatusTermina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TerminRepository extends JpaRepository<Termin, Integer> {

    List<Termin> findByDoktorID(Integer doktorID);

    @Query("SELECT t FROM Termin t WHERE t.doktorID = :doktorID AND t.statusTermina = 'DOSTUPAN'")
    List<Termin> findSlobodniTerminiByDoktor(@Param("doktorID") Integer doktorID);

    @Query("SELECT COUNT(t) > 0 FROM Termin t WHERE t.terminID = :terminID AND t.statusTermina = 'DOSTUPAN'")
    boolean checkTerminIsAvailable(@Param("terminID") Integer terminID);

    @Query("SELECT t FROM Termin t WHERE t.doktorID = :doktorID AND t.datum = :datum ORDER BY t.vrijeme")
    List<Termin> findByDoktorIDAndDatum(@Param("doktorID") Integer doktorID, @Param("datum") LocalDate datum);

    List<Termin> findByDoktorIDAndStatusTermina(Integer doktorID, StatusTermina statusTermina);

    @Query("SELECT COUNT(t) FROM Termin t WHERE t.doktorID = :doktorID AND t.datum = :datum AND t.statusTermina = 'ZAKAZAN'")
    long countZauzetihTerminaByDoktorAndDatum(@Param("doktorID") Integer doktorID, @Param("datum") LocalDate datum);

    Optional<Termin> findByDoktorIDAndDatumAndVrijeme(Integer doktorID, LocalDate datum, LocalTime vrijeme);
}