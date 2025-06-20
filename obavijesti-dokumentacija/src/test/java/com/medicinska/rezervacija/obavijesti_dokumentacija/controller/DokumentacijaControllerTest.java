/*package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DokumentacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.DokumentacijaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DokumentacijaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DokumentacijaService medicalRecordService;

    @InjectMocks
    private DokumentacijaController dokumentacijaController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(dokumentacijaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAddMedicalRecord() throws Exception {
        // Kreiramo DTO koji ćemo poslati u POST zahtev
        DokumentacijaDTO dokumentacijaDTO = new DokumentacijaDTO();
        dokumentacijaDTO.setDokumentacijaID(1L);
        dokumentacijaDTO.setPacijentID(100L);
        dokumentacijaDTO.setDoktorID(200L);
        dokumentacijaDTO.setPregledID(300L);
        dokumentacijaDTO.setTipDokumenta("CT Scan");
        dokumentacijaDTO.setNazivDokumenta("CT Thorax");
        dokumentacijaDTO.setDatumKreiranja(LocalDate.now());
        dokumentacijaDTO.setPristup("PUBLIC");

        // Mockovanje servisa da vrati isti DTO nakon dodavanja
        when(medicalRecordService.createDokumentacija(dokumentacijaDTO)).thenReturn(dokumentacijaDTO);

        // Slanje POST zahteva i provera odgovora
        mockMvc.perform(post("/medicinska-dokumentacija/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dokumentacijaDTO)))
                .andExpect(status().isCreated())  // Očekujemo status 201 Created
                .andExpect(jsonPath("$.dokumentacijaID").value(1))
                .andExpect(jsonPath("$.pacijentID").value(100))
                .andExpect(jsonPath("$.doktorID").value(200))
                .andExpect(jsonPath("$.pregledID").value(300))
                .andExpect(jsonPath("$.tipDokumenta").value("CT Scan"))
                .andExpect(jsonPath("$.nazivDokumenta").value("CT Thorax"))
                .andExpect(jsonPath("$.pristup").value("PUBLIC"));
    }

    @Test
    public void testGetMedicalRecordById() throws Exception {
        // Kreiramo DTO koji ćemo koristiti za povratak medicinske dokumentacije
        DokumentacijaDTO dokumentacijaDTO = new DokumentacijaDTO();
        dokumentacijaDTO.setDokumentacijaID(1L);
        dokumentacijaDTO.setPacijentID(100L);
        dokumentacijaDTO.setDoktorID(200L);
        dokumentacijaDTO.setPregledID(300L);
        dokumentacijaDTO.setTipDokumenta("CT Scan");
        dokumentacijaDTO.setNazivDokumenta("CT Thorax");
        dokumentacijaDTO.setDatumKreiranja(LocalDate.now());
        dokumentacijaDTO.setPristup("PUBLIC");

        // Mockovanje servisa da vrati DTO za zadati ID
        when(medicalRecordService.getDokumentacijaById(1L)).thenReturn(dokumentacijaDTO);

        // Slanje GET zahteva i provera odgovora
        mockMvc.perform(get("/medicinska-dokumentacija/1"))
                .andExpect(status().isOk())  // Očekujemo status 200 OK
                .andExpect(jsonPath("$.dokumentacijaID").value(1))
                .andExpect(jsonPath("$.pacijentID").value(100))
                .andExpect(jsonPath("$.doktorID").value(200))
                .andExpect(jsonPath("$.pregledID").value(300))
                .andExpect(jsonPath("$.tipDokumenta").value("CT Scan"))
                .andExpect(jsonPath("$.nazivDokumenta").value("CT Thorax"))
                .andExpect(jsonPath("$.pristup").value("PUBLIC"));
    }
}
*/