//package com.medicinska.rezervacija.termini_pregledi.service;
//
//import com.medicinska.rezervacija.termini_pregledi.dto.TerminDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Termin;
//import com.medicinska.rezervacija.termini_pregledi.model.Doktor;
//import com.medicinska.rezervacija.termini_pregledi.repository.TerminRepository;
//import com.medicinska.rezervacija.termini_pregledi.repository.DoktorRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TerminServiceTest {
//
//    @Mock
//    private TerminRepository terminRepository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @Mock
//    private DoktorRepository doktorRepository; // Mock DoktorRepository
//
//    @InjectMocks
//    private TerminService terminService;
//
//    private Termin termin;
//    private TerminDTO terminDTO;
//    private Doktor doktor;
//
//    private static final Integer TEST_TERMIN_ID = 1;
//    private static final Integer TEST_DOKTOR_ID = 5;
//    private static final String TEST_DATUM = "2025-04-10";
//    private static final String TEST_VRIJEME = "10:30:00";
//
//    @BeforeEach
//    void setUp() {
//        doktor = new Doktor();
//        doktor.setDoktorID(TEST_DOKTOR_ID);
//
//        termin = new Termin(TEST_TERMIN_ID, doktor, Date.valueOf(TEST_DATUM), Time.valueOf(TEST_VRIJEME), true);
//        terminDTO = new TerminDTO();
//        terminDTO.setDoktorID(TEST_DOKTOR_ID);
//        terminDTO.setDatum(Date.valueOf(TEST_DATUM));
//        terminDTO.setVrijeme(Time.valueOf(TEST_VRIJEME));
//        terminDTO.setDostupnost(true);
//
//        when(modelMapper.map(termin, TerminDTO.class)).thenReturn(terminDTO);
//        when(modelMapper.map(terminDTO, Termin.class)).thenReturn(termin);
//    }
//
//    @Test
//    void testGetAllTerminiPaged() {
//        List<Termin> terminList = Arrays.asList(termin);
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Termin> terminPage = new PageImpl<>(terminList, pageable, terminList.size());
//        when(terminRepository.findAll(pageable)).thenReturn(terminPage);
//
//        Page<TerminDTO> result = terminService.getAllTerminiPaged(pageable);
//
//        Assertions.assertEquals(1, result.getContent().size());
//        Assertions.assertEquals(terminDTO, result.getContent().get(0));
//        verify(terminRepository, times(1)).findAll(pageable);
//        verify(modelMapper, times(1)).map(termin, TerminDTO.class);
//    }
//
//    @Test
//    void testGetTerminById_Found() {
//        when(terminRepository.findById(TEST_TERMIN_ID)).thenReturn(Optional.of(termin));
//
//        Optional<TerminDTO> result = terminService.getTerminById(TEST_TERMIN_ID);
//
//        Assertions.assertTrue(result.isPresent());
//        Assertions.assertEquals(terminDTO, result.get());
//        verify(terminRepository, times(1)).findById(TEST_TERMIN_ID);
//        verify(modelMapper, times(1)).map(termin, TerminDTO.class);
//    }
//
//    @Test
//    void testGetTerminById_NotFound() {
//        when(terminRepository.findById(2)).thenReturn(Optional.empty());
//
//        Optional<TerminDTO> result = terminService.getTerminById(2);
//
//        Assertions.assertFalse(result.isPresent());
//        verify(terminRepository, times(1)).findById(2);
//        verify(modelMapper, never()).map(any(), any());
//    }
//
//    @Test
//    void testSaveTermin() {
//        when(doktorRepository.findById(TEST_DOKTOR_ID)).thenReturn(Optional.of(doktor)); // Mock doktorRepository
//        when(terminRepository.save(any(Termin.class))).thenReturn(termin);
//
//        TerminDTO result = terminService.saveTermin(terminDTO);
//
//        Assertions.assertNotNull(result);
//        Assertions.assertEquals(terminDTO, result);
//        verify(terminRepository, times(1)).save(any(Termin.class));
//        verify(modelMapper, times(1)).map(termin, TerminDTO.class);
//        verify(modelMapper, times(1)).map(terminDTO, Termin.class);
//        verify(doktorRepository, times(1)).findById(TEST_DOKTOR_ID); // Verify doktorRepository interaction
//    }
//
//    @Test
//    void testUpdateTermin_Found() {
//        when(terminRepository.findById(TEST_TERMIN_ID)).thenReturn(Optional.of(termin));
//        when(terminRepository.save(any(Termin.class))).thenReturn(termin);
//
//        Optional<TerminDTO> result = terminService.updateTermin(TEST_TERMIN_ID, terminDTO);
//
//        Assertions.assertTrue(result.isPresent());
//        Assertions.assertEquals(terminDTO, result.get());
//        verify(terminRepository, times(1)).findById(TEST_TERMIN_ID);
//        verify(terminRepository, times(1)).save(any(Termin.class));
//        verify(modelMapper, times(1)).map(termin, TerminDTO.class);
//    }
//
//    @Test
//    void testUpdateTermin_NotFound() {
//        when(terminRepository.findById(2)).thenReturn(Optional.empty());
//
//        Optional<TerminDTO> result = terminService.updateTermin(2, terminDTO);
//
//        Assertions.assertFalse(result.isPresent());
//        verify(terminRepository, times(1)).findById(2);
//        verify(terminRepository, never()).save(any(Termin.class));
//    }
//
//    @Test
//    void testDeleteTermin_Found() {
//        when(terminRepository.existsById(TEST_TERMIN_ID)).thenReturn(true);
//        doNothing().when(terminRepository).deleteById(TEST_TERMIN_ID);
//
//        boolean result = terminService.deleteTermin(TEST_TERMIN_ID);
//
//        Assertions.assertTrue(result);
//        verify(terminRepository, times(1)).deleteById(TEST_TERMIN_ID);
//    }
//
//    @Test
//    void testDeleteTermin_NotFound() {
//        when(terminRepository.existsById(2)).thenReturn(false);
//
//        boolean result = terminService.deleteTermin(2);
//
//        Assertions.assertFalse(result);
//        verify(terminRepository, never()).deleteById(any());
//    }
//
//    @Test
//    void testGetSlobodniTerminiForDoktor() {
//        List<Termin> terminList = Arrays.asList(termin);
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Termin> terminPage = new PageImpl<>(terminList, pageable, terminList.size());
//        when(terminRepository.findSlobodniTerminiByDoktor(TEST_DOKTOR_ID, pageable)).thenReturn(terminPage);
//
//        Page<TerminDTO> result = terminService.getSlobodniTerminiForDoktor(TEST_DOKTOR_ID, pageable);
//
//        Assertions.assertEquals(1, result.getContent().size());
//        Assertions.assertEquals(terminDTO, result.getContent().get(0));
//        verify(terminRepository, times(1)).findSlobodniTerminiByDoktor(TEST_DOKTOR_ID, pageable);
//        verify(modelMapper, times(1)).map(termin, TerminDTO.class);
//    }
//
//    @Test
//    void testCheckTerminAvailability_Available() {
//        when(terminRepository.isTerminAvailable(TEST_TERMIN_ID)).thenReturn(true);
//
//        boolean result = terminService.checkTerminAvailability(TEST_TERMIN_ID);
//
//        Assertions.assertTrue(result);
//        verify(terminRepository, times(1)).isTerminAvailable(TEST_TERMIN_ID);
//    }
//
//    @Test
//    void testCheckTerminAvailability_Unavailable() {
//        when(terminRepository.isTerminAvailable(TEST_TERMIN_ID)).thenReturn(false);
//
//        boolean result = terminService.checkTerminAvailability(TEST_TERMIN_ID);
//
//        Assertions.assertFalse(result);
//        verify(terminRepository, times(1)).isTerminAvailable(TEST_TERMIN_ID);
//    }
//
//    @Test
//    void testGetTerminiForDoktor() {
//        List<Termin> terminList = Arrays.asList(termin);
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<Termin> terminPage = new PageImpl<>(terminList, pageable, terminList.size());
//        when(terminRepository.findByDoktor_DoktorID(TEST_DOKTOR_ID, pageable)).thenReturn(terminPage);
//        when(modelMapper.map(any(), eq(TerminDTO.class))).thenReturn(terminDTO);
//
//        Page<TerminDTO> result = terminService.getTerminiForDoktor(TEST_DOKTOR_ID, pageable);
//
//        Assertions.assertEquals(1, result.getContent().size());
//        Assertions.assertEquals(terminDTO, result.getContent().get(0));
//        verify(terminRepository, times(1)).findByDoktor_DoktorID(TEST_DOKTOR_ID, pageable);
//        verify(modelMapper, times(1)).map(any(), eq(TerminDTO.class));
//    }
//}