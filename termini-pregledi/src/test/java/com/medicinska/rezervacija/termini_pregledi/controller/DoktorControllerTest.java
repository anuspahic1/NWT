//// DoktorControllerTest.java
//package com.medicinska.rezervacija.termini_pregledi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medicinska.rezervacija.termini_pregledi.dto.DoktorDTO;
//import com.medicinska.rezervacija.termini_pregledi.service.DoktorService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class DoktorControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Mock
//    private DoktorService doktorService;
//
//    @InjectMocks
//    private DoktorController doktorController;
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private DoktorDTO doktorDTO;
//
//    // Definicija konstanti za testiranje
//    private static final Integer TEST_DOKTOR_ID = 9;
//    private static final String TEST_DOKTOR_IME = "Ajla";
//    private static final Integer TEST_DOKTOR_ID_TO_UPDATE = 1;
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this); // Inicijalizacija Mockito mockova
//        doktorDTO = new DoktorDTO(TEST_DOKTOR_ID, TEST_DOKTOR_IME, "Ajlin", "Sarajevo", "08:00-17:00", 8, 4.5, "ime.prezime@example.com", "+38763331222", "sifra1234");
//        mockMvc = MockMvcBuilders.standaloneSetup(doktorController).build();
//    }
//
//    @Test
//    public void testGetAllDoktori() throws Exception {
//        when(doktorService.getAllDoktori()).thenReturn(List.of(doktorDTO));
//
//        mockMvc.perform(get("/api/doktori"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$[0].ime").value(TEST_DOKTOR_IME));
//    }
//
//    @Test
//    public void testGetDoktorById() throws Exception {
//        when(doktorService.getDoktorById(TEST_DOKTOR_ID)).thenReturn(Optional.of(doktorDTO));
//
//        mockMvc.perform(get("/api/doktori/" + TEST_DOKTOR_ID))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.ime").value(TEST_DOKTOR_IME));
//    }
//
//    @Test
//    public void testCreateDoktor() throws Exception {
//        when(doktorService.saveDoktor(any())).thenReturn(doktorDTO);
//        String requestBody = objectMapper.writeValueAsString(doktorDTO);
//
//        mockMvc.perform(post("/api/doktori")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))
//                .andExpect(jsonPath("$.ime").value(TEST_DOKTOR_IME));
//    }
//
//    @Test
//    public void testUpdateDoktor() throws Exception {
//        when(doktorService.updateDoktor(eq(TEST_DOKTOR_ID_TO_UPDATE), any())).thenReturn(Optional.of(doktorDTO));
//        String requestBody = objectMapper.writeValueAsString(doktorDTO);
//
//        mockMvc.perform(put("/api/doktori/" + TEST_DOKTOR_ID_TO_UPDATE)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBody))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.doktorID").value(TEST_DOKTOR_ID))  // Provjera da ID ostaje isti
//                .andExpect(jsonPath("$.ime").value(TEST_DOKTOR_IME));
//    }
//
//    @Test
//    public void testDeleteDoktor() throws Exception {
//        when(doktorService.deleteDoktor(TEST_DOKTOR_ID_TO_UPDATE)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/doktori/" + TEST_DOKTOR_ID_TO_UPDATE))
//                .andExpect(status().isNoContent());
//
//        verify(doktorService, times(1)).deleteDoktor(TEST_DOKTOR_ID_TO_UPDATE);
//    }
//
//    @Test
//    public void testGetDoktorById_NotFound() throws Exception {
//        when(doktorService.getDoktorById(TEST_DOKTOR_ID)).thenReturn(Optional.empty());
//
//        mockMvc.perform(get("/api/doktori/" + TEST_DOKTOR_ID))
//                .andExpect(status().isNotFound());
//    }
//}