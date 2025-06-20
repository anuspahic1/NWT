package com.medicinska.rezervacija.korisnici_doktori.repository;

import com.medicinska.rezervacija.korisnici_doktori.model.MedicinskaHistorija;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MedicinskaHistorijaRepository extends JpaRepository<MedicinskaHistorija, Integer> {

    List<MedicinskaHistorija> findByPacijentOrderByDatumZapisivanjaDesc(Pacijent pacijent);

    List<MedicinskaHistorija> findByDoktorDoktorIDOrderByDatumZapisivanjaDesc(Integer doktorID);
}
