/*package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Pacijent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PacijentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PacijentServiceTest {

    @Mock
    private PacijentRepository pacijentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PacijentService pacijentService;

    private Pacijent pacijent;
    private PacijentDTO pacijentDTO;

    @BeforeEach
    void setUp() {
        pacijent = new Pacijent(1L, "Marko", "Marković", "marko.markovic@example.com", "0123456789", "Adresa 123", "lozinka123", null, "Muško");
        pacijentDTO = new PacijentDTO(1L, "Marko", "Marković", "marko.markovic@example.com", "0123456789", "Adresa 123", "lozinka123", null, "Muško");
    }

    @Test
    void testGetAllPacijenti() {
        when(pacijentRepository.findAll()).thenReturn(List.of(pacijent));
        when(modelMapper.map(pacijent, PacijentDTO.class)).thenReturn(pacijentDTO);

        List<PacijentDTO> pacijentiDTO = pacijentService.getAllPacijenti();

        assertNotNull(pacijentiDTO);
        assertEquals(1, pacijentiDTO.size());
        assertEquals("Marko", pacijentiDTO.get(0).getIme());

        verify(pacijentRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
    }

    @Test
    void testGetPacijentById() {
        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(modelMapper.map(pacijent, PacijentDTO.class)).thenReturn(pacijentDTO);

        Optional<PacijentDTO> result = pacijentService.getPacijentById(1L);

        assertTrue(result.isPresent());
        assertEquals("Marko", result.get().getIme());

        verify(pacijentRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
    }

    @Test
    void testSavePacijent() {
        when(modelMapper.map(pacijentDTO, Pacijent.class)).thenReturn(pacijent);
        when(pacijentRepository.save(pacijent)).thenReturn(pacijent);
        when(modelMapper.map(pacijent, PacijentDTO.class)).thenReturn(pacijentDTO);

        PacijentDTO savedPacijent = pacijentService.savePacijent(pacijentDTO);

        assertNotNull(savedPacijent);
        assertEquals("Marko", savedPacijent.getIme());

        verify(modelMapper, times(1)).map(pacijentDTO, Pacijent.class);
        verify(pacijentRepository, times(1)).save(pacijent);
        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
    }

    @Test
    void testDeletePacijent() {
        doNothing().when(pacijentRepository).deleteById(1L);

        pacijentService.deletePacijent(1L);

        verify(pacijentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdatePacijent() {
        Pacijent updatedPacijent = new Pacijent(1L, "NovoIme", "NovoPrezime", "novo.email@example.com", "0987654321", "Nova adresa", "novalozinka", null, "Žensko");
        PacijentDTO updatedPacijentDTO = new PacijentDTO(1L, "NovoIme", "NovoPrezime", "novo.email@example.com", "0987654321", "Nova adresa", "novalozinka", null, "Žensko");

        when(pacijentRepository.findById(1L)).thenReturn(Optional.of(pacijent));
        when(pacijentRepository.save(any(Pacijent.class))).thenReturn(updatedPacijent);
        when(modelMapper.map(updatedPacijent, PacijentDTO.class)).thenReturn(updatedPacijentDTO);

        PacijentDTO result = pacijentService.updatePacijent(1L, updatedPacijentDTO);

        assertNotNull(result);
        assertEquals("NovoIme", result.getIme());
        assertEquals("NovoPrezime", result.getPrezime());

        verify(pacijentRepository, times(1)).findById(1L);
        verify(pacijentRepository, times(1)).save(any(Pacijent.class));
        verify(modelMapper, times(1)).map(updatedPacijent, PacijentDTO.class);
    }

    @Test
    void testExistsById() {
        when(pacijentRepository.existsById(1L)).thenReturn(true);

        boolean exists = pacijentService.existsById(1L);

        assertTrue(exists);

        verify(pacijentRepository, times(1)).existsById(1L);
    }

    @Test
    void testCountPacijenti() {
        when(pacijentRepository.count()).thenReturn(5L);

        long count = pacijentService.countPacijenti();

        assertEquals(5L, count);

        verify(pacijentRepository, times(1)).count();
    }
}
*/