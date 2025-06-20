package com.medicinska.rezervacija.obavijesti_dokumentacija.repository;

import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Poruka;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PorukaRepository extends JpaRepository<Poruka, Long> {

    @EntityGraph(attributePaths = "replies")
    @Override
    Optional<Poruka> findById(Long id);

    @EntityGraph(attributePaths = "replies")
    List<Poruka> findByReceiverIdAndReceiverTypeOrderByTimestampDesc(Long receiverId, Notifikacija.Uloga receiverType);

     @EntityGraph(attributePaths = "replies")
    List<Poruka> findBySenderIdAndSenderTypeOrderByTimestampDesc(Long senderId, Notifikacija.Uloga senderType);

    @EntityGraph(attributePaths = "replies")
    List<Poruka> findBySenderIdOrReceiverIdOrderByTimestampDesc(Long userId1, Long userId2); // userId1 i userId2 bi trebali biti isti ID, koristi se za OR uvjet
}