//package com.medicinska.rezervacija.termini_pregledi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medicinska.rezervacija.termini_pregledi.dto.PregledDTO;
//import com.medicinska.rezervacija.termini_pregledi.service.PregledService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.sql.Date;
//import java.sql.Time;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class PregledControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private PregledService pregledService;
//
//    @InjectMocks
//    private PregledController pregledController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private PregledDTO pregledDTO;
//
//    @BeforeEach
//    void setUp() {
//        pregledDTO = new PregledDTO();
//        pregledDTO.setPregledID(1);
//        pregledDTO.setDatumPregleda(Date.valueOf("2025-04-10"));
//        pregledDTO.setVrijemePregleda(Time.valueOf("10:30:00"));
//        pregledDTO.setStatus("zakazan");
//    }
//
//    @Test
//    void testGetPregledById_Found() throws Exception {
//        when(pregledService.getPregledById(1)).thenReturn(Optional.of(pregledDTO));
//
//        mockMvc.perform(get("/api/pregledi/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.pregledID").value(1))
//                .andExpect(jsonPath("$.status").value("zakazan"));
//    }
//
//    @Test
//    void testGetPregledById_NotFound() throws Exception {
//        when(pregledService.getPregledById(1)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/pregledi/1"))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void testCreatePregled() throws Exception {
//        when(pregledService.savePregled(any())).thenReturn(pregledDTO);
//
//        String requestBody = """
//                {
//                    "datumPregleda": "2025-04-10",
//                    "vrijemePregleda": "10:30:00",
//                    "status": "zakazan"
//                }
//                """;
//
//        mockMvc.perform(post("/api/pregledi")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.status").value("zakazan"));
//    }
//
//    @Test
//    void testDeletePregled_Success() throws Exception {
//        when(pregledService.deletePregled(1)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/pregledi/1"))
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void testDeletePregled_NotFound() throws Exception {
//        when(pregledService.deletePregled(1)).thenReturn(false);
//
//        mockMvc.perform(delete("/api/pregledi/1"))
//                .andExpect(status().isNotFound());
//    }
//}