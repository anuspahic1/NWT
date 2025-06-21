package com.system_events.repository; // Paket za vaš repozitorijum

import com.system_events.entity.EventEntity; // Import entiteta
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Označava da je ovo Spring repozitorijum
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    // Spring Data JPA automatski generiše osnovne CRUD metode (save, findById, findAll, delete, itd.)
    // Možete dodati i prilagođene metode ako vam zatrebaju (npr. findByMicroserviceName)
}
