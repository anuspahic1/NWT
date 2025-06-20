//package com.medicinska.rezervacija.termini_pregledi.service;
//
//import com.medicinska.rezervacija.termini_pregledi.dto.SpecijalizacijaDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Specijalizacija;
//import com.medicinska.rezervacija.termini_pregledi.repository.SpecijalizacijaRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class SpecijalizacijaServiceTest {
//
//    @Mock
//    private SpecijalizacijaRepository specijalizacijaRepository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private SpecijalizacijaService specijalizacijaService;
//
//    private Specijalizacija specijalizacija;
//    private SpecijalizacijaDTO specijalizacijaDTO;
//
//    @BeforeEach
//    void setUp() {
//        specijalizacija = new Specijalizacija(1, "Kardiologija", null);
//        specijalizacijaDTO = new SpecijalizacijaDTO(1, "Kardiologija");
//    }
//
//    @Test
//    void testCreateSpecijalizacija() {
//        when(modelMapper.map(specijalizacijaDTO, Specijalizacija.class)).thenReturn(specijalizacija);
//        when(specijalizacijaRepository.save(specijalizacija)).thenReturn(specijalizacija);
//        when(modelMapper.map(specijalizacija, SpecijalizacijaDTO.class)).thenReturn(specijalizacijaDTO);
//
//        SpecijalizacijaDTO result = specijalizacijaService.createSpecijalizacija(specijalizacijaDTO);
//        assertNotNull(result);
//        assertEquals("Kardiologija", result.getNazivSpecijalizacije());
//    }
//
//    @Test
//    void testGetSpecijalizacijaById() {
//        when(specijalizacijaRepository.findById(1)).thenReturn(Optional.of(specijalizacija));
//        when(modelMapper.map(specijalizacija, SpecijalizacijaDTO.class)).thenReturn(specijalizacijaDTO);
//
//        SpecijalizacijaDTO result = specijalizacijaService.getSpecijalizacijaById(1);
//        assertNotNull(result);
//        assertEquals("Kardiologija", result.getNazivSpecijalizacije());
//    }
//
//    @Test
//    void testGetAllSpecijalizacije() {
//        when(specijalizacijaRepository.findAll()).thenReturn(Arrays.asList(specijalizacija));
//        when(modelMapper.map(specijalizacija, SpecijalizacijaDTO.class)).thenReturn(specijalizacijaDTO);
//
//        List<SpecijalizacijaDTO> result = specijalizacijaService.getAllSpecijalizacije();
//        assertEquals(1, result.size());
//        assertEquals("Kardiologija", result.get(0).getNazivSpecijalizacije());
//    }
//
//    @Test
//    void testUpdateSpecijalizacija() {
//        when(specijalizacijaRepository.findById(1)).thenReturn(Optional.of(specijalizacija));
//        when(specijalizacijaRepository.save(any(Specijalizacija.class))).thenReturn(specijalizacija);
//        when(modelMapper.map(specijalizacija, SpecijalizacijaDTO.class)).thenReturn(specijalizacijaDTO);
//
//        SpecijalizacijaDTO updatedDTO = new SpecijalizacijaDTO(1, "Neurologija");
//        SpecijalizacijaDTO result = specijalizacijaService.updateSpecijalizacija(1, updatedDTO);
//        assertNotNull(result);
//    }
//
//    @Test
//    void testDeleteSpecijalizacija() {
//        when(specijalizacijaRepository.existsById(1)).thenReturn(true);
//        doNothing().when(specijalizacijaRepository).deleteById(1);
//
//        boolean result = specijalizacijaService.deleteSpecijalizacija(1);
//        assertTrue(result);
//    }
//}
