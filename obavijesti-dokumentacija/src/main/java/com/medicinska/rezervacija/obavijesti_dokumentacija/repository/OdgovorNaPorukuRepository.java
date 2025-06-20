package com.medicinska.rezervacija.obavijesti_dokumentacija.repository;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.OdgovorNaPoruku;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OdgovorNaPorukuRepository extends JpaRepository<OdgovorNaPoruku, Long> {

    List<OdgovorNaPoruku> findByParentMessagePorukaIDOrderByTimestampAsc(Long parentMessageId);
}
