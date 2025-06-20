package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.clients.KorisniciDoktoriClient;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.NotifikacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.NotifikacijaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotifikacijaRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final KorisniciDoktoriClient korisniciDoktoriClient; // KorisniciDoktoriClient nije definiran u vašem kodu. Pretpostavljam da ga koristite za dohvaćanje imena pacijenata/doktora.

    @Autowired
    public NotificationService(NotifikacijaRepository notificationRepository,
                               ModelMapper modelMapper,
                               KorisniciDoktoriClient korisniciDoktoriClient) {
        this.notificationRepository = notificationRepository;
        this.modelMapper = modelMapper;
        this.korisniciDoktoriClient = korisniciDoktoriClient;
    }

    public NotifikacijaDTO sendNotification(Long korisnikID, String message, Notifikacija.Uloga uloga) {
        Notifikacija notifikacija = new Notifikacija();
        notifikacija.setKorisnikID(korisnikID);
        notifikacija.setSadrzaj(message);
        notifikacija.setStatus(Notifikacija.StatusNotifikacije.POSLANO);
        notifikacija.setDatumSlanja(LocalDateTime.now());
        notifikacija.setUloga(uloga);

        Notifikacija savedNotifikacija = notificationRepository.save(notifikacija);
        return modelMapper.map(savedNotifikacija, NotifikacijaDTO.class);
    }


    public NotifikacijaDTO getNotificationById(Long id) {
        Notifikacija notifikacija = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notifikacija nije pronađena"));

        return modelMapper.map(notifikacija, NotifikacijaDTO.class);
    }

    public List<NotifikacijaDTO> getNotificationsForUser(Long korisnikID, Notifikacija.Uloga uloga) {
        List<Notifikacija> notifications = notificationRepository.findByKorisnikIDAndUlogaOrderByDatumSlanjaDesc(korisnikID, uloga);
        return notifications.stream()
                .map(notif -> modelMapper.map(notif, NotifikacijaDTO.class))
                .collect(Collectors.toList());
    }


    public NotifikacijaDTO markNotificationAsRead(Long notificationId) {
        Notifikacija notifikacija = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notifikacija nije pronađena s ID-om: " + notificationId));
        notifikacija.setStatus(Notifikacija.StatusNotifikacije.PROCITANO);
        Notifikacija updatedNotifikacija = notificationRepository.save(notifikacija);
        return modelMapper.map(updatedNotifikacija, NotifikacijaDTO.class);
    }

    public String getKorisnikIme(Long korisnikID, Notifikacija.Uloga uloga) {
        if (uloga == Notifikacija.Uloga.PACIJENT) {
            try {
                 PacijentDTO pacijent = korisniciDoktoriClient.getPacijentById(korisnikID);
                return pacijent.getIme() + " " + pacijent.getPrezime();
            } catch (Exception e) {
                System.err.println("Greška prilikom dohvaćanja pacijenta sa ID " + korisnikID + ": " + e.getMessage());
                return "Nepoznati pacijent";
            }
        } else if (uloga == Notifikacija.Uloga.DOKTOR) {
            try {
                return "Doktor";
            } catch (Exception e) {
                System.err.println("Greška prilikom dohvaćanja doktora sa ID " + korisnikID + ": " + e.getMessage());
                return "Nepoznati doktor";
            }
        }
        return "Nepoznati korisnik";
    }
}
