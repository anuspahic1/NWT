/*package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.NotifikacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Pacijent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.NotifikacijaRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PacijentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotifikacijaRepository notificationRepository;

    @Mock
    private PacijentRepository pacijentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private NotificationService notificationService;

    private Pacijent pacijent;
    private Notifikacija notifikacija;
    private NotifikacijaDTO notifikacijaDTO;

    @BeforeEach
    void setUp() {
        // Kreiranje testnog Pacijent objekta
        pacijent = new Pacijent();
        pacijent.setIme("Ivan");
        pacijent.setPrezime("Ivić");

        // Kreiranje testnog Notifikacija objekta
        notifikacija = new Notifikacija();
        notifikacija.setKorisnik(pacijent);
        notifikacija.setSadrzaj("Test poruka");
        notifikacija.setStatus(Notifikacija.StatusNotifikacije.POSLANO);
        notifikacija.setDatumSlanja(LocalDateTime.now());

    }

    @Test
    void testSendNotification() {
        // Mockiranje odgovora iz PacijentRepository
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(notificationRepository.save(any(Notifikacija.class))).thenReturn(notifikacija);
        when(modelMapper.map(notifikacija, NotifikacijaDTO.class)).thenReturn(notifikacijaDTO);

        // Pozivanje metode
        NotifikacijaDTO result = notificationService.sendNotification(1L, "Test poruka");

        // Provjera da li je rezultat ispravno mapiran
        assertNotNull(result);
        assertEquals("Test poruka", result.getSadrzaj());
        assertEquals(1L, result.getKorisnikID());
        assertEquals(Notifikacija.StatusNotifikacije.POSLANO, result.getStatus());

        // Verifikacija da je pozvana metoda za spremanje notifikacije
        verify(notificationRepository, times(1)).save(any(Notifikacija.class));
    }

    @Test
    void testSendNotificationPacijentNotFound() {
        // Mockiranje slučaja kada pacijent nije pronađen
        when(pacijentRepository.findById(1L)).thenReturn(Optional.empty());

        // Provjera da li baca iznimku
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.sendNotification(1L, "Test poruka");
        });

        assertEquals("Pacijent s ID-om 1 nije pronađen", exception.getMessage());
    }

    @Test
    void testGetNotificationById() {
        // Mockiranje odgovora iz NotifikacijaRepository
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notifikacija));
        when(modelMapper.map(notifikacija, NotifikacijaDTO.class)).thenReturn(notifikacijaDTO);

        // Pozivanje metode
        NotifikacijaDTO result = notificationService.getNotificationById(1L);

        // Provjera da li je rezultat ispravno mapiran
        assertNotNull(result);
        assertEquals("Test poruka", result.getSadrzaj());
        assertEquals(1L, result.getKorisnikID());
        assertEquals(Notifikacija.StatusNotifikacije.POSLANO, result.getStatus());
    }

    @Test
    void testGetNotificationByIdNotFound() {
        // Mockiranje slučaja kada notifikacija nije pronađena
        when(notificationRepository.findById(1L)).thenReturn(Optional.empty());

        // Pozivanje metode
        NotifikacijaDTO result = notificationService.getNotificationById(1L);

        // Provjera da li je rezultat null
        assertNull(result);
    }
}
*/