/*package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DokumentacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Dokumentacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Pacijent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Doktor;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DokumentacijaRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PacijentRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DoktorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DokumentacijaServiceTest {

    @Mock
    private DokumentacijaRepository dokumentacijaRepository;

    @Mock
    private PacijentRepository pacijentRepository;

    @Mock
    private DoktorRepository doktorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DokumentacijaService dokumentacijaService;

    private DokumentacijaDTO dokumentacijaDTO;
    private Pacijent pacijent;
    private Doktor doktor;
    private Dokumentacija dokumentacija;

    @BeforeEach
    void setUp() {
        // Postavljanje objekata
        pacijent = new Pacijent();
        pacijent.setIme("Ivan");
        pacijent.setPrezime("Ivić");

        doktor = new Doktor();
        doktor.setIme("Marko");
        doktor.setPrezime("Marković");

        dokumentacija = new Dokumentacija();
        dokumentacija.setPacijent(pacijent);
        dokumentacija.setDoktor(doktor);
    }

    @Test
    void testCreateDokumentacija() {
        // Mockiranje odgovora iz repository-ja
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));
        when(modelMapper.map(dokumentacijaDTO, Dokumentacija.class)).thenReturn(dokumentacija);
        when(dokumentacijaRepository.save(dokumentacija)).thenReturn(dokumentacija);
        when(modelMapper.map(dokumentacija, DokumentacijaDTO.class)).thenReturn(dokumentacijaDTO);

        // Pozivanje metode
        DokumentacijaDTO result = dokumentacijaService.createDokumentacija(dokumentacijaDTO);

        // Provjere
        assertNotNull(result);
        assertEquals(dokumentacijaDTO.getPacijentID(), result.getPacijentID());
        assertEquals(dokumentacijaDTO.getDoktorID(), result.getDoktorID());
        verify(dokumentacijaRepository, times(1)).save(any(Dokumentacija.class));
    }

    @Test
    void testCreateDokumentacijaPacijentNotFound() {
        // Mockiranje slučaja kada pacijent nije pronađen
        when(pacijentRepository.findById(1L)).thenReturn(Optional.empty());
        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));

        // Provjera da li baca iznimku
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dokumentacijaService.createDokumentacija(dokumentacijaDTO);
        });

        assertEquals("Pacijent ili doktor nisu pronađeni!", exception.getMessage());
    }

    @Test
    void testCreateDokumentacijaDoktorNotFound() {
        // Mockiranje slučaja kada doktor nije pronađen
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(doktorRepository.findById(1L)).thenReturn(Optional.empty());

        // Provjera da li baca iznimku
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            dokumentacijaService.createDokumentacija(dokumentacijaDTO);
        });

        assertEquals("Pacijent ili doktor nisu pronađeni!", exception.getMessage());
    }

    @Test
    void testGetDokumentacijaById() {
        // Mockiranje odgovora iz repository-ja
        when(dokumentacijaRepository.findById(1L)).thenReturn(Optional.of(dokumentacija));
        when(modelMapper.map(dokumentacija, DokumentacijaDTO.class)).thenReturn(dokumentacijaDTO);

        // Pozivanje metode
        DokumentacijaDTO result = dokumentacijaService.getDokumentacijaById(1L);

        // Provjere
        assertNotNull(result);
        assertEquals(dokumentacijaDTO.getPacijentID(), result.getPacijentID());
        assertEquals(dokumentacijaDTO.getDoktorID(), result.getDoktorID());
    }

    @Test
    void testGetDokumentacijaByIdDokumentacijaNotFound() {
        // Mockiranje slučaja kada dokumentacija nije pronađena
        when(dokumentacijaRepository.findById(1L)).thenReturn(Optional.empty());

        // Pozivanje metode
        DokumentacijaDTO result = dokumentacijaService.getDokumentacijaById(1L);

        // Provjere
        assertNull(result);
    }
}
*/