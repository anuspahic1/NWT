/*package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PregledDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Pacijent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Doktor;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Pregled;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PregledRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PacijentRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DoktorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
public class PregledServiceTest {

    @Mock
    private PregledRepository pregledRepository;

    @Mock
    private PacijentRepository pacijentRepository;

    @Mock
    private DoktorRepository doktorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PregledService pregledService;

    private Pregled pregled;
    private PregledDTO pregledDTO;
    private Pacijent pacijent;
    private Doktor doktor;

    @BeforeEach
    void setUp() {
        pacijent = new Pacijent();
        pacijent.setIme("Marko");
        pacijent.setPrezime("Markovic");

        doktor = new Doktor();
        doktor.setIme("Dr. Jovan");
        doktor.setPrezime("Jovanovic");

        pregled = new Pregled();
        pregled.setPacijent(pacijent);
        pregled.setDoktor(doktor);

    }



    @Test
    void testGetPregledById() {
        // Arrange
        when(pregledRepository.findById(1L)).thenReturn(Optional.of(pregled));

        // Act
        Optional<PregledDTO> result = pregledService.getPregledById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(pregledDTO, result.get());
    }

    @Test
    void testCreatePregled() {
        // Arrange
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));
        when(pregledRepository.save(pregled)).thenReturn(pregled);

        // Act
        PregledDTO result = pregledService.createPregled(pregledDTO);

        // Assert
        assertNotNull(result);
        assertEquals(pregledDTO, result);
    }

    @Test
    void testCreatePregled_PacijentOrDoktorNotFound() {
        // Arrange
        when(pacijentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pregledService.createPregled(pregledDTO);
        });
        assertEquals("Pacijent ili doktor nisu pronađeni!", exception.getMessage());
    }

    @Test
    void testUpdatePregled() {
        // Arrange
        when(pregledRepository.findById(1L)).thenReturn(Optional.of(pregled));
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));
        when(pregledRepository.save(pregled)).thenReturn(pregled);

        // Act
        PregledDTO result = pregledService.updatePregled(1L, pregledDTO);

        // Assert
        assertNotNull(result);
        assertEquals(pregledDTO, result);
    }

    @Test
    void testUpdatePregled_NotFound() {
        // Arrange
        when(pregledRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        PregledDTO result = pregledService.updatePregled(1L, pregledDTO);

        // Assert
        assertNull(result);  // Pregled should be null if not found
    }

    @Test
    void testDeletePregled() {
        // Arrange
        when(pregledRepository.findById(1L)).thenReturn(Optional.of(pregled));

        // Act
        boolean result = pregledService.deletePregled(1L);

        // Assert
        assertTrue(result);
        verify(pregledRepository, times(1)).delete(pregled);
    }

    @Test
    void testDeletePregled_NotFound() {
        // Arrange
        when(pregledRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = pregledService.deletePregled(1L);

        // Assert
        assertFalse(result);
        verify(pregledRepository, never()).delete(any(Pregled.class));
    }
}
*/