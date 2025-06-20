//package com.medicinska.rezervacija.termini_pregledi.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.medicinska.rezervacija.termini_pregledi.dto.SpecijalizacijaDTO;
//import com.medicinska.rezervacija.termini_pregledi.service.SpecijalizacijaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//class SpecijalizacijaControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Mock
//    private SpecijalizacijaService specijalizacijaService;
//
//    @InjectMocks
//    private SpecijalizacijaController specijalizacijaController;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private SpecijalizacijaDTO specijalizacijaDTO;
//
//    @BeforeEach
//    void setUp() {
//        specijalizacijaDTO = new SpecijalizacijaDTO(1, "Kardiologija");
//    }
//
//    @Test
//    void testCreateSpecijalizacija() throws Exception {
//        when(specijalizacijaService.createSpecijalizacija(any(SpecijalizacijaDTO.class))).thenReturn(specijalizacijaDTO);
//
//        mockMvc.perform(post("/api/specijalizacije")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(specijalizacijaDTO)))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.nazivSpecijalizacije").value("Kardiologija"));
//    }
//
//    @Test
//    void testGetSpecijalizacijaById() throws Exception {
//        when(specijalizacijaService.getSpecijalizacijaById(1)).thenReturn(specijalizacijaDTO);
//
//        mockMvc.perform(get("/api/specijalizacije/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nazivSpecijalizacije").value("Kardiologija"));
//    }
//
//    @Test
//    void testGetAllSpecijalizacije() throws Exception {
//        List<SpecijalizacijaDTO> specijalizacije = Arrays.asList(
//                new SpecijalizacijaDTO(1, "Kardiologija"),
//                new SpecijalizacijaDTO(2, "Neurologija")
//        );
//        when(specijalizacijaService.getAllSpecijalizacije()).thenReturn(specijalizacije);
//
//        mockMvc.perform(get("/api/specijalizacije"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.size()").value(2))
//                .andExpect(jsonPath("$[0].nazivSpecijalizacije").value("Kardiologija"))
//                .andExpect(jsonPath("$[1].nazivSpecijalizacije").value("Neurologija"));
//    }
//
//    @Test
//    void testUpdateSpecijalizacija() throws Exception {
//        SpecijalizacijaDTO updatedDTO = new SpecijalizacijaDTO(1, "Neurologija");
//        when(specijalizacijaService.updateSpecijalizacija(Mockito.eq(1), any(SpecijalizacijaDTO.class)))
//                .thenReturn(updatedDTO);
//
//        mockMvc.perform(put("/api/specijalizacije/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updatedDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nazivSpecijalizacije").value("Neurologija"));
//    }
//
//    @Test
//    void testDeleteSpecijalizacija() throws Exception {
//        when(specijalizacijaService.deleteSpecijalizacija(1)).thenReturn(true);
//
//        mockMvc.perform(delete("/api/specijalizacije/1"))
//                .andExpect(status().isNoContent());
//    }
//}
