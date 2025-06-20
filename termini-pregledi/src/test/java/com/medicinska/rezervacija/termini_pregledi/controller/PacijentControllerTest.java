//// PacijentControllerTest.java
//package com.medicinska.rezervacija.termini_pregledi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medicinska.rezervacija.termini_pregledi.dto.PacijentDTO;
//import com.medicinska.rezervacija.termini_pregledi.model.Pacijent;
//import com.medicinska.rezervacija.termini_pregledi.service.PacijentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.validation.BindingResult;
//import org.springframework.validation.FieldError;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class PacijentControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private PacijentService pacijentService;
//
//    @InjectMocks
//    private PacijentController pacijentController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
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
//    }
//
//    @Test
//    void testGetAllPacijenti() throws Exception {
//        List<PacijentDTO> pacijenti = Collections.singletonList(pacijentDTO);
//        when(pacijentService.getAllPacijenti()).thenReturn(pacijenti);
//
//        mockMvc.perform(get("/api/pacijenti"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ime").value(TEST_IME))
//                .andExpect(jsonPath("$[0].prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testGetPacijentById_Found() throws Exception {
//        when(pacijentService.getPacijentById(TEST_PACIJENT_ID)).thenReturn(Optional.of(pacijentDTO));
//
//        mockMvc.perform(get("/api/pacijenti/" + TEST_PACIJENT_ID))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.ime").value(TEST_IME))
//                .andExpect(jsonPath("$.prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testGetPacijentById_NotFound() throws Exception {
//        when(pacijentService.getPacijentById(TEST_PACIJENT_ID)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/pacijenti/" + TEST_PACIJENT_ID))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testGetPacijentById_WithPregledi() throws Exception {
//        when(pacijentService.getPacijentById(TEST_PACIJENT_ID, true)).thenReturn(Optional.of(pacijentDTO));
//
//        mockMvc.perform(get("/api/pacijenti/" + TEST_PACIJENT_ID)
//                        .param("withPregledi", "true"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.ime").value(TEST_IME))
//                .andExpect(jsonPath("$.prezime").value(TEST_PREZIME));
//    }
//
//
//    @Test
//    void testCreatePacijent_Success() throws Exception {
//        when(pacijentService.savePacijent(any(PacijentDTO.class))).thenReturn(pacijentDTO);
//
//        mockMvc.perform(post("/api/pacijenti")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(pacijentDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.ime").value(TEST_IME))
//                .andExpect(jsonPath("$.prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testCreatePacijent_InvalidInput() throws Exception {
//        PacijentDTO invalidDTO = new PacijentDTO(); // Kreiramo DTO sa nevalidnim podacima
//        BindingResult result = mock(BindingResult.class);
//        when(result.hasErrors()).thenReturn(true);
//        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("objectName", "fieldName", "errorMessage")));
//
//        mockMvc.perform(post("/api/pacijenti")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testUpdatePacijent_Success() throws Exception {
//        when(pacijentService.updatePacijent(eq(TEST_PACIJENT_ID), any(PacijentDTO.class))).thenReturn(Optional.of(pacijentDTO));
//
//        mockMvc.perform(put("/api/pacijenti/" + TEST_PACIJENT_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(pacijentDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.ime").value(TEST_IME))
//                .andExpect(jsonPath("$.prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testUpdatePacijent_InvalidInput() throws Exception {
//        PacijentDTO invalidDTO = new PacijentDTO();
//        BindingResult result = mock(BindingResult.class);
//        when(result.hasErrors()).thenReturn(true);
//        when(result.getFieldErrors()).thenReturn(Collections.singletonList(new FieldError("objectName", "fieldName", "errorMessage")));
//
//        mockMvc.perform(put("/api/pacijenti/" + TEST_PACIJENT_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDTO)))
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void testUpdatePacijent_NotFound() throws Exception {
//        when(pacijentService.updatePacijent(eq(TEST_PACIJENT_ID), any(PacijentDTO.class))).thenReturn(Optional.empty());
//
//        mockMvc.perform(put("/api/pacijenti/" + TEST_PACIJENT_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(pacijentDTO)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testDeletePacijent_Success() throws Exception {
//        when(pacijentService.deletePacijent(TEST_PACIJENT_ID)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/pacijenti/" + TEST_PACIJENT_ID))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void testDeletePacijent_NotFound() throws Exception {
//        when(pacijentService.deletePacijent(TEST_PACIJENT_ID)).thenReturn(false);
//
//        mockMvc.perform(delete("/api/pacijenti/" + TEST_PACIJENT_ID))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testGetPacijentiPaged() throws Exception {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<PacijentDTO> pacijentPage = new PageImpl<>(Collections.singletonList(pacijentDTO), pageable, 1);
//        when(pacijentService.getAllPacijentiPaged(any(Pageable.class))).thenReturn(pacijentPage);
//
//        mockMvc.perform(get("/api/pacijenti/paged")
//                        .param("page", "0")
//                        .param("size", "5"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].ime").value(TEST_IME))
//                .andExpect(jsonPath("$.content[0].prezime").value(TEST_PREZIME))
//                .andExpect(jsonPath("$.totalPages").value(1));
//    }
//
//    @Test
//    void testGetPacijentiByIme() throws Exception {
//        List<PacijentDTO> pacijenti = Collections.singletonList(pacijentDTO);
//        when(pacijentService.getPacijentiByIme(TEST_IME)).thenReturn(pacijenti);
//
//        mockMvc.perform(get("/api/pacijenti/pretraga/" + TEST_IME))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ime").value(TEST_IME));
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime() throws Exception {
//        List<PacijentDTO> pacijenti = Collections.singletonList(pacijentDTO);
//        when(pacijentService.searchPacijentiByImePrezime(TEST_IME, TEST_PREZIME)).thenReturn(pacijenti);
//
//        mockMvc.perform(get("/api/pacijenti/pretraga")
//                        .param("ime", TEST_IME)
//                        .param("prezime", TEST_PREZIME))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ime").value(TEST_IME))
//                .andExpect(jsonPath("$[0].prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_OnlyIme() throws Exception {
//        List<PacijentDTO> pacijenti = Collections.singletonList(pacijentDTO);
//        when(pacijentService.searchPacijentiByImePrezime(TEST_IME, "")).thenReturn(pacijenti);
//
//        mockMvc.perform(get("/api/pacijenti/pretraga")
//                        .param("ime", TEST_IME))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].ime").value(TEST_IME));
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_OnlyPrezime() throws Exception {
//        List<PacijentDTO> pacijenti = Collections.singletonList(pacijentDTO);
//        when(pacijentService.searchPacijentiByImePrezime("", TEST_PREZIME)).thenReturn(pacijenti);
//
//        mockMvc.perform(get("/api/pacijenti/pretraga")
//                        .param("prezime", TEST_PREZIME))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].prezime").value(TEST_PREZIME));
//    }
//
//    @Test
//    void testSearchPacijentiByImePrezime_NoResults() throws Exception {
//        when(pacijentService.searchPacijentiByImePrezime(TEST_IME, TEST_PREZIME)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/api/pacijenti/pretraga")
//                        .param("ime", TEST_IME)
//                        .param("prezime", TEST_PREZIME))
//                .andExpect(status().isNoContent());
//    }
//}