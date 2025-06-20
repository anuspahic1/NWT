//// DoktorServiceTest.java
//package com.medicinska.rezervacija.termini_pregledi.service;
//
//import com.medicinska.rezervacija.termini_pregledi.dto.DoktorDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Doktor;
//import com.medicinska.rezervacija.termini_pregledi.repository.DoktorRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.junit.jupiter.api.Assertions.*;
//
//class DoktorServiceTest {
//
//    @Mock
//    private DoktorRepository doktorRepository;
//
//    @InjectMocks
//    private DoktorService doktorService;
//
//    private DoktorDTO doktorDTO;
//
//    private static final Integer TEST_DOKTOR_ID = 1;
//    private static final String TEST_DOKTOR_IME = "Marko";
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//        doktorDTO = new DoktorDTO(TEST_DOKTOR_ID, TEST_DOKTOR_IME, "Marković", "Zagreb", "8-16", 10, 4.5, "marko@email.com", "+385987654321", "lozinka123");
//    }
//
//    @Test
//    public void testSaveDoktor() {
//        Doktor doktor = new Doktor(TEST_DOKTOR_ID, TEST_DOKTOR_IME, "Marković", "Zagreb", "8-16", 10, 4.5, "marko@email.com", "+385987654321", "lozinka123");
//        when(doktorRepository.save(any(Doktor.class))).thenReturn(doktor);
//
//        DoktorDTO result = doktorService.saveDoktor(doktorDTO);
//
//        assertNotNull(result);
//        assertEquals(TEST_DOKTOR_ID, result.getDoktorID());
//        assertEquals(TEST_DOKTOR_IME, result.getIme());
//    }
//
//    @Test
//    public void testGetDoktorById() {
//        Doktor doktor = new Doktor(TEST_DOKTOR_ID, TEST_DOKTOR_IME, "Marković", "Zagreb", "8-16", 10, 4.5, "marko@email.com", "+385987654321", "lozinka123");
//        when(doktorRepository.findById(TEST_DOKTOR_ID)).thenReturn(Optional.of(doktor));
//
//        Optional<DoktorDTO> result = doktorService.getDoktorById(TEST_DOKTOR_ID);
//
//        assertTrue(result.isPresent());
//        assertEquals(TEST_DOKTOR_IME, result.get().getIme());
//    }
//
//    @Test
//    public void testDeleteDoktor() {
//        when(doktorRepository.findById(TEST_DOKTOR_ID)).thenReturn(Optional.of(new Doktor())); // Changed from existsById to findById
//        doNothing().when(doktorRepository).delete(any(Doktor.class)); // Changed from deleteById to delete
//
//        boolean result = doktorService.deleteDoktor(TEST_DOKTOR_ID);
//
//        assertTrue(result);
//        verify(doktorRepository, times(1)).findById(TEST_DOKTOR_ID);
//        verify(doktorRepository, times(1)).delete(any(Doktor.class));
//    }
//}