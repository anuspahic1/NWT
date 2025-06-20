//package com.medicinska.rezervacija.termini_pregledi.service;
//
//import com.medicinska.rezervacija.termini_pregledi.dto.PregledDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Pregled;
//import com.medicinska.rezervacija.termini_pregledi.repository.PregledRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PregledServiceTest {
//
//    @Mock
//    private PregledRepository pregledRepository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private PregledService pregledService;
//
//    private Pregled pregled;
//    private PregledDTO pregledDTO;
//
//    @BeforeEach
//    void setUp() {
//        pregled = new Pregled();
//        pregled.setPregledID(1);
//        pregled.setDatumPregleda(Date.valueOf("2025-04-10"));
//        pregled.setVrijemePregleda(Time.valueOf("10:30:00"));
//        pregled.setStatus(Pregled.Status.zakazan);
//
//        pregledDTO = new PregledDTO();
//        pregledDTO.setPregledID(1);
//        pregledDTO.setDatumPregleda(Date.valueOf("2025-04-10"));
//        pregledDTO.setVrijemePregleda(Time.valueOf("10:30:00"));
//        pregledDTO.setStatus("zakazan");
//    }
//
//    @Test
//    void testGetPregledById_Found() {
//        when(pregledRepository.findById(1)).thenReturn(Optional.of(pregled));
//        when(modelMapper.map(pregled, PregledDTO.class)).thenReturn(pregledDTO);
//
//        Optional<PregledDTO> result = pregledService.getPregledById(1);
//
//        assertTrue(result.isPresent());
//        assertEquals("zakazan", result.get().getStatus());
//    }
//
//    @Test
//    void testGetPregledById_NotFound() {
//        when(pregledRepository.findById(1)).thenReturn(Optional.empty());
//
//        Optional<PregledDTO> result = pregledService.getPregledById(1);
//
//        assertFalse(result.isPresent());
//    }
//
//    @Test
//    void testSavePregled() {
//        when(modelMapper.map(pregledDTO, Pregled.class)).thenReturn(pregled);
//        when(pregledRepository.save(pregled)).thenReturn(pregled);
//        when(modelMapper.map(pregled, PregledDTO.class)).thenReturn(pregledDTO);
//
//        PregledDTO savedDTO = pregledService.savePregled(pregledDTO);
//
//        assertNotNull(savedDTO);
//        assertEquals("zakazan", savedDTO.getStatus());
//    }
//
//    @Test
//    void testDeletePregled_Success() {
//        when(pregledRepository.existsById(1)).thenReturn(true);
//        doNothing().when(pregledRepository).deleteById(1);
//
//        assertTrue(pregledService.deletePregled(1));
//    }
//
//    @Test
//    void testDeletePregled_NotFound() {
//        when(pregledRepository.existsById(1)).thenReturn(false);
//
//        assertFalse(pregledService.deletePregled(1));
//    }
//}
