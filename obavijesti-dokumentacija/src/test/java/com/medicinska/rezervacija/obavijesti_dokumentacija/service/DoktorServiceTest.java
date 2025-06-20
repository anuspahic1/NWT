/*package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Doktor;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.DoktorRepository;
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
class DoktorServiceTest {

    @Mock
    private DoktorRepository doktorRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DoktorService doktorService;

    private Doktor doktor;
    private DoktorDTO doktorDTO;

    @BeforeEach
    void setUp() {
        doktor = new Doktor(1L, "Dr. Marko", "Marković", "dr.marko.markovic@example.com");
        doktorDTO = new DoktorDTO(1L, "Dr. Marko", "Marković", "dr.marko.markovic@example.com");
    }

    @Test
    void testGetAllDoktori() {
        when(doktorRepository.findAll()).thenReturn(List.of(doktor));
        when(modelMapper.map(doktor, DoktorDTO.class)).thenReturn(doktorDTO);

        List<DoktorDTO> doktoriDTO = doktorService.getAllDoktori();

        assertNotNull(doktoriDTO);
        assertEquals(1, doktoriDTO.size());
        assertEquals("Dr. Marko", doktoriDTO.get(0).getIme());

        verify(doktorRepository, times(1)).findAll();
        verify(modelMapper, times(1)).map(doktor, DoktorDTO.class);
    }

    @Test
    void testGetDoktorById() {
        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));
        when(modelMapper.map(doktor, DoktorDTO.class)).thenReturn(doktorDTO);

        Optional<DoktorDTO> result = doktorService.getDoktorById(1L);

        assertTrue(result.isPresent());
        assertEquals("Dr. Marko", result.get().getIme());

        verify(doktorRepository, times(1)).findById(1L);
        verify(modelMapper, times(1)).map(doktor, DoktorDTO.class);
    }

    @Test
    void testSaveDoktor() {
        when(modelMapper.map(doktor, DoktorDTO.class)).thenReturn(doktorDTO);
        when(doktorRepository.save(doktor)).thenReturn(doktor);
        when(modelMapper.map(doktor, DoktorDTO.class)).thenReturn(doktorDTO);

        DoktorDTO savedDoktor = doktorService.saveDoktor(doktor);

        assertNotNull(savedDoktor);
        assertEquals("Dr. Marko", savedDoktor.getIme());

        verify(modelMapper, times(1)).map(doktor, DoktorDTO.class);
        verify(doktorRepository, times(1)).save(doktor);
    }

    @Test
    void testDeleteDoktor() {
        doNothing().when(doktorRepository).deleteById(1L);

        doktorService.deleteDoktor(1L);

        verify(doktorRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateDoktor() {
        Doktor updatedDoktor = new Doktor(1L, "Dr. Novo Ime", "Novo Prezime", "dr.novo.ime@example.com");
        DoktorDTO updatedDoktorDTO = new DoktorDTO(1L, "Dr. Novo Ime", "Novo Prezime", "dr.novo.ime@example.com");

        when(doktorRepository.findById(1L)).thenReturn(Optional.of(doktor));
        when(doktorRepository.save(any(Doktor.class))).thenReturn(updatedDoktor);
        when(modelMapper.map(updatedDoktor, DoktorDTO.class)).thenReturn(updatedDoktorDTO);

        DoktorDTO result = doktorService.updateDoktor(1L, updatedDoktor);

        assertNotNull(result);
        assertEquals("Dr. Novo Ime", result.getIme());
        assertEquals("Novo Prezime", result.getPrezime());

        verify(doktorRepository, times(1)).findById(1L);
        verify(doktorRepository, times(1)).save(any(Doktor.class));
        verify(modelMapper, times(1)).map(updatedDoktor, DoktorDTO.class);
    }
}
*/