package com.medicinska.rezervacija.termini_pregledi.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign Client za komunikaciju sa ObavijestiDokumentacija servisom.
 *
 * 'name' atribut mora odgovarati 'spring.application.name'
 * ObavijestiDokumentacija mikroservisa (npr. 'OBAVIJESTI-DOKUMENTACIJA' kako je registriran u Eureka serveru).
 * 'path' atribut ovdje nije potreban, jer će se cijela relativna putanja definirati u @PostMapping.
 */
@FeignClient(name = "OBAVIJESTI-DOKUMENTACIJA", path = "/obavijesti-dokumentacija/api/obavijesti") // Ovo bi trebalo biti ime servisa u Eureka-i
public interface NotificationClient {

    /**
     * Šalje notifikaciju ObavijestiDokumentacija servisu.
     * Putanja u @PostMapping mora tačno odgovarati kombinaciji
     * @RequestMapping u NotificationControlleru i @PostMappingu metode unutar tog kontrolera
     * (e.g., @RequestMapping("/api/obavijesti") + @PostMapping("/notifikacije/send") = "/api/obavijesti/notifikacije/send").
     */
    @PostMapping("/notifikacije/send") // <-- ISPRAVLJENA PUTANJA
    void sendNotification(@RequestParam("korisnikID") Long korisnikID,
                          @RequestParam("message") String message,
                          @RequestParam("uloga") String uloga);
}
