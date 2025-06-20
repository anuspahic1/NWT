package com.medicinska.rezervacija.obavijesti_dokumentacija.repository;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotifikacijaRepository extends JpaRepository<Notifikacija, Long> {

    List<Notifikacija> findByKorisnikIDAndUlogaOrderByDatumSlanjaDesc(Long korisnikID, Notifikacija.Uloga uloga);

    List<Notifikacija> findByKorisnikID(Long korisnikID);
}
