//package com.medicinska.rezervacija.termini_pregledi.service;
//
//import com.medicinska.rezervacija.termini_pregledi.dto.PacijentDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Pacijent;
//import com.medicinska.rezervacija.termini_pregledi.repository.PacijentRepository;
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
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PacijentServiceTest {
//
//    @Mock
//    private PacijentRepository pacijentRepository;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private PacijentService pacijentService;
//
//    private Pacijent pacijent;
//    private PacijentDTO pacijentDTO;
//    private static final Integer TEST_PACIJENT_ID = 1;
//    private static final String TEST_IME = "Marko";
//    private static final String TEST_PREZIME = "Marić";
//    private static final String TEST_EMAIL = "marko.maric@example.com";
//    private static final String TEST_TELEFON = "+385911234567";
//    private static final String TEST_ADRESA = "Zagreb, Hrvatska";
//    private static final LocalDate TEST_DATUM_RODJENJA = LocalDate.of(1990, 1, 15);
//    private static final Pacijent.Spol TEST_SPOL = Pacijent.Spol.M;
//    private static final String TEST_LOZINKA = "lozinka123";
//
//    @BeforeEach
//    void setUp() {
//        pacijent = new Pacijent();
//        pacijent.setPacijentID(TEST_PACIJENT_ID);
//        pacijent.setIme(TEST_IME);
//        pacijent.setPrezime(TEST_PREZIME);
//        pacijent.setEmail(TEST_EMAIL);
//        pacijent.setTelefon(TEST_TELEFON);
//        pacijent.setAdresa(TEST_ADRESA);
//        pacijent.setDatumRodjenja(TEST_DATUM_RODJENJA);
//        pacijent.setSpol(TEST_SPOL);
//        pacijent.setLozinka(TEST_LOZINKA);
//
//        pacijentDTO = new PacijentDTO();
//        pacijentDTO.setPacijentID(TEST_PACIJENT_ID);
//        pacijentDTO.setIme(TEST_IME);
//        pacijentDTO.setPrezime(TEST_PREZIME);
//        pacijentDTO.setEmail(TEST_EMAIL);
//        pacijentDTO.setTelefon(TEST_TELEFON);
//        pacijentDTO.setAdresa(TEST_ADRESA);
//        pacijentDTO.setDatumRodjenja(TEST_DATUM_RODJENJA);
//        pacijentDTO.setSpol(TEST_SPOL);
//        pacijentDTO.setLozinka(TEST_LOZINKA);
//
//        when(modelMapper.map(pacijent, PacijentDTO.class)).thenReturn(pacijentDTO);
//        when(modelMapper.map(pacijentDTO, Pacijent.class)).thenReturn(pacijent);
//    }
//
//    @Test
//    void testGetAllPacijenti() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        when(pacijentRepository.findAll()).thenReturn(pacijenti);
//
//        List<PacijentDTO> result = pacijentService.getAllPacijenti();
//
//        assertEquals(1, result.size());
//        assertEquals(pacijentDTO, result.get(0));
//        verify(pacijentRepository, times(1)).findAll();
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testGetPacijentById_Found() {
//        when(pacijentRepository.findById(TEST_PACIJENT_ID)).thenReturn(Optional.of(pacijent));
//
//        Optional<PacijentDTO> result = pacijentService.getPacijentById(TEST_PACIJENT_ID);
//
//        assertTrue(result.isPresent());
//        assertEquals(pacijentDTO, result.get());
//        verify(pacijentRepository, times(1)).findById(TEST_PACIJENT_ID);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testGetPacijentById_NotFound() {
//        when(pacijentRepository.findById(TEST_PACIJENT_ID)).thenReturn(Optional.empty());
//
//        Optional<PacijentDTO> result = pacijentService.getPacijentById(TEST_PACIJENT_ID);
//
//        assertFalse(result.isPresent());
//        verify(pacijentRepository, times(1)).findById(TEST_PACIJENT_ID);
//        verify(modelMapper, never()).map(any(), any());
//    }
//
//    @Test
//    void testGetPacijentById_WithPregledi() {
//        when(pacijentRepository.findWithPreglediByPacijentID(TEST_PACIJENT_ID)).thenReturn(Optional.of(pacijent));
//
//        Optional<PacijentDTO> result = pacijentService.getPacijentById(TEST_PACIJENT_ID, true);
//
//        assertTrue(result.isPresent());
//        assertEquals(pacijentDTO, result.get());
//        verify(pacijentRepository, times(1)).findWithPreglediByPacijentID(TEST_PACIJENT_ID);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testSavePacijent() {
//        when(pacijentRepository.save(any(Pacijent.class))).thenReturn(pacijent);
//
//        PacijentDTO result = pacijentService.savePacijent(pacijentDTO);
//
//        assertNotNull(result);
//        assertEquals(pacijentDTO, result);
//        verify(pacijentRepository, times(1)).save(any(Pacijent.class));
//        verify(modelMapper, times(1)).map(pacijentDTO, Pacijent.class);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testUpdatePacijent_Found() {
//        when(pacijentRepository.findById(TEST_PACIJENT_ID)).thenReturn(Optional.of(pacijent));
//        when(pacijentRepository.save(any(Pacijent.class))).thenReturn(pacijent);
//
//        Optional<PacijentDTO> result = pacijentService.updatePacijent(TEST_PACIJENT_ID, pacijentDTO);
//
//        assertTrue(result.isPresent());
//        assertEquals(pacijentDTO, result.get());
//        verify(pacijentRepository, times(1)).findById(TEST_PACIJENT_ID);
//        verify(pacijentRepository, times(1)).save(any(Pacijent.class));
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testUpdatePacijent_NotFound() {
//        when(pacijentRepository.findById(TEST_PACIJENT_ID)).thenReturn(Optional.empty());
//
//        Optional<PacijentDTO> result = pacijentService.updatePacijent(TEST_PACIJENT_ID, pacijentDTO);
//
//        assertFalse(result.isPresent());
//        verify(pacijentRepository, times(1)).findById(TEST_PACIJENT_ID);
//        verify(pacijentRepository, never()).save(any(Pacijent.class));
//    }
//
//    @Test
//    void testDeletePacijent_Found() {
//        when(pacijentRepository.existsById(TEST_PACIJENT_ID)).thenReturn(true);
//        doNothing().when(pacijentRepository).deleteById(TEST_PACIJENT_ID);
//
//        boolean result = pacijentService.deletePacijent(TEST_PACIJENT_ID);
//
//        assertTrue(result);
//        verify(pacijentRepository, times(1)).deleteById(TEST_PACIJENT_ID);
//    }
//
//    @Test
//    void testDeletePacijent_NotFound() {
//        when(pacijentRepository.existsById(TEST_PACIJENT_ID)).thenReturn(false);
//
//        boolean result = pacijentService.deletePacijent(TEST_PACIJENT_ID);
//
//        assertFalse(result);
//        verify(pacijentRepository, times(0)).deleteById(TEST_PACIJENT_ID);
//    }
//
//    @Test
//    void testGetAllPacijentiPaged() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Pacijent> pacijentPage = new PageImpl<>(pacijenti, pageable, 1);
//        when(pacijentRepository.findAll(pageable)).thenReturn(pacijentPage);
//
//        Page<PacijentDTO> result = pacijentService.getAllPacijentiPaged(pageable);
//
//        assertEquals(1, result.getTotalElements());
//        assertEquals(pacijentDTO, result.getContent().get(0));
//        verify(pacijentRepository, times(1)).findAll(pageable);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testGetPacijentiByIme() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        when(pacijentRepository.findByIme(TEST_IME)).thenReturn(pacijenti);
//
//        List<PacijentDTO> result = pacijentService.getPacijentiByIme(TEST_IME);
//
//        assertEquals(1, result.size());
//        assertEquals(pacijentDTO, result.get(0));
//        verify(pacijentRepository, times(1)).findByIme(TEST_IME);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        when(pacijentRepository.findByImeAndPrezimeContainingIgnoreCase(TEST_IME, TEST_PREZIME)).thenReturn(pacijenti);
//
//        List<PacijentDTO> result = pacijentService.searchPacijentiByImePrezime(TEST_IME, TEST_PREZIME);
//
//        assertEquals(1, result.size());
//        assertEquals(pacijentDTO, result.get(0));
//        verify(pacijentRepository, times(1)).findByImeAndPrezimeContainingIgnoreCase(TEST_IME, TEST_PREZIME);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_OnlyIme() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        when(pacijentRepository.findByImeAndPrezimeContainingIgnoreCase(TEST_IME, "")).thenReturn(pacijenti);
//
//        List<PacijentDTO> result = pacijentService.searchPacijentiByImePrezime(TEST_IME, "");
//
//        assertEquals(1, result.size());
//        assertEquals(pacijentDTO, result.get(0));
//        verify(pacijentRepository, times(1)).findByImeAndPrezimeContainingIgnoreCase(TEST_IME, "");
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_OnlyPrezime() {
//        List<Pacijent> pacijenti = Collections.singletonList(pacijent);
//        when(pacijentRepository.findByImeAndPrezimeContainingIgnoreCase("", TEST_PREZIME)).thenReturn(pacijenti);
//
//        List<PacijentDTO> result = pacijentService.searchPacijentiByImePrezime("", TEST_PREZIME);
//
//        assertEquals(1, result.size());
//        assertEquals(pacijentDTO, result.get(0));
//        verify(pacijentRepository, times(1)).findByImeAndPrezimeContainingIgnoreCase("", TEST_PREZIME);
//        verify(modelMapper, times(1)).map(pacijent, PacijentDTO.class);
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_NoResults() {
//        when(pacijentRepository.findByImeAndPrezimeContainingIgnoreCase(TEST_IME, TEST_PREZIME)).thenReturn(Collections.emptyList());
//
//        List<PacijentDTO> result = pacijentService.searchPacijentiByImePrezime(TEST_IME, TEST_PREZIME);
//
//        assertTrue(result.isEmpty());
//        verify(pacijentRepository, times(1)).findByImeAndPrezimeContainingIgnoreCase(TEST_IME, TEST_PREZIME);
//    }
//}
//
