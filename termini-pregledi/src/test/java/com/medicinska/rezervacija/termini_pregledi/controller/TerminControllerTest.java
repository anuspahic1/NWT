//// TerminControllerTest.java
//package com.medicinska.rezervacija.termini_pregledi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medicinska.rezervacija.termini_pregledi.dto.TerminDTO;
//import com.medicinska.rezervacija.termini_pregledi.service.TerminService;
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
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class TerminControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private TerminService terminService;
//
//    @InjectMocks
//    private TerminController terminController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private TerminDTO terminDTO;
//    private static final Integer TEST_TERMIN_ID = 1;
//    private static final Integer TEST_DOKTOR_ID = 10;
//    private static final String TEST_DATUM = "2024-12-01";
//    private static final String TEST_VRIJEME = "12:00:00";
//
//    @BeforeEach
//    void setUp() {
//        terminDTO = new TerminDTO();
//        terminDTO.setDoktorID(TEST_DOKTOR_ID);
//        terminDTO.setDatum(Date.valueOf(TEST_DATUM));
//        terminDTO.setVrijeme(Time.valueOf(TEST_VRIJEME));
//        terminDTO.setDostupnost(true);
//    }
//
//    @Test
//    void testGetTerminById_Found() throws Exception {
//        when(terminService.getTerminById(TEST_TERMIN_ID)).thenReturn(Optional.of(terminDTO));
//
//        mockMvc.perform(get("/api/termini/" + TEST_TERMIN_ID))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.dostupnost").value(true));
//    }
//
//    @Test
//    void testGetTerminById_NotFound() throws Exception {
//        when(terminService.getTerminById(TEST_TERMIN_ID)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/termini/" + TEST_TERMIN_ID))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testCreateTermin_Success() throws Exception {
//        when(terminService.saveTermin(any())).thenReturn(terminDTO);
//        String requestBody = objectMapper.writeValueAsString(terminDTO);
//
//        mockMvc.perform(post("/api/termini")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.dostupnost").value(true));
//    }
//
//    @Test
//    void testUpdateTermin_Success() throws Exception {
//        when(terminService.updateTermin(eq(TEST_TERMIN_ID), any())).thenReturn(Optional.of(terminDTO));
//        String requestBody = objectMapper.writeValueAsString(terminDTO);
//
//        mockMvc.perform(put("/api/termini/" + TEST_TERMIN_ID)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.dostupnost").value(true));
//    }
//
//    @Test
//    void testDeleteTermin_Success() throws Exception {
//        when(terminService.deleteTermin(TEST_TERMIN_ID)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/termini/" + TEST_TERMIN_ID))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void testDeleteTermin_NotFound() throws Exception {
//        when(terminService.deleteTermin(TEST_TERMIN_ID)).thenReturn(false);
//
//        mockMvc.perform(delete("/api/termini/" + TEST_TERMIN_ID))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testGetAllTerminiPaged() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<TerminDTO> terminPage = new PageImpl<>(Collections.singletonList(terminDTO), pageable, 1);
//        when(terminService.getAllTerminiPaged(any(Pageable.class))).thenReturn(terminPage);
//
//        mockMvc.perform(get("/api/termini")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.content[0].datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.content[0].vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.content[0].dostupnost").value(true));
//    }
//
//    @Test
//    void testGetSlobodniTerminiForDoktor() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<TerminDTO> terminPage = new PageImpl<>(Collections.singletonList(terminDTO), pageable, 1);
//        when(terminService.getSlobodniTerminiForDoktor(eq(TEST_DOKTOR_ID), any(Pageable.class))).thenReturn(terminPage);
//
//        mockMvc.perform(get("/api/termini/slobodni/" + TEST_DOKTOR_ID)
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.content[0].datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.content[0].vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.content[0].dostupnost").value(true));
//    }
//
//    @Test
//    void testCheckTerminAvailability() throws Exception {
//        when(terminService.checkTerminAvailability(TEST_TERMIN_ID)).thenReturn(true);
//
//        mockMvc.perform(get("/api/termini/dostupnost/" + TEST_TERMIN_ID))
//                .andExpect(status().isOk())
//                .andExpect(content().string("true"));
//    }
//
//    @Test
//    void testGetTerminiForDoktor() throws Exception {
//        Pageable pageable = PageRequest.of(0, 10);
//        Page<TerminDTO> terminPage = new PageImpl<>(Collections.singletonList(terminDTO), pageable, 1);
//        when(terminService.getTerminiForDoktor(eq(TEST_DOKTOR_ID), any(Pageable.class))).thenReturn(terminPage);
//
//        mockMvc.perform(get("/api/termini/doktor/" + TEST_DOKTOR_ID)
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.content[0].datum").value(TEST_DATUM))
//                .andExpect(jsonPath("$.content[0].vrijeme").value(TEST_VRIJEME))
//                .andExpect(jsonPath("$.content[0].dostupnost").value(true));
//    }
//
//    @Test
//    void testCreateTermin_InvalidInput() throws Exception {
//        TerminDTO invalidTerminDTO = new TerminDTO(); // TerminDTO with null values
//        String requestBody = objectMapper.writeValueAsString(invalidTerminDTO);
//
//        mockMvc.perform(post("/api/termini")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isBadRequest()); // Expecting HTTP 400 for invalid input
//    }
//}