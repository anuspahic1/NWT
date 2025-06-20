package com.medicinska.rezervacija.obavijesti_dokumentacija.repository;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Racun;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.RacunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RacunRepository extends JpaRepository<Racun, Long> {

    List<Racun> findByPacijentIDOrderByDatumIzdavanjaDesc(Long pacijentID);

    List<Racun> findByDoktorIDOrderByDatumIzdavanjaDesc(Long doktorID);

    List<Racun> findByStatusOrderByDatumIzdavanjaDesc(RacunStatus status);
}
