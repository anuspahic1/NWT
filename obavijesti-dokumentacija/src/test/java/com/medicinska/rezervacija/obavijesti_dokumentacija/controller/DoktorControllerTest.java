/*package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.DoktorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class DoktorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private DoktorService doktorService;

    @InjectMocks
    private DoktorController doktorController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(doktorController).build();
    }

    @Test
    public void testGetAllDoktori_EmptyList() throws Exception {
        // Mockovanje doktora u servisu
        when(doktorService.getAllDoktori()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/doktori"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllDoktori_FilledList() throws Exception {
        // Kreiramo listu doktora sa četiri argumenta
        List<DoktorDTO> doktorDTOList = List.of(new DoktorDTO(1L, "Dr. Ime", "Prezime", "ime.prezime@domen.com"));

        // Mockovanje doktora u servisu
        when(doktorService.getAllDoktori()).thenReturn(doktorDTOList);

        mockMvc.perform(get("/doktori"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ime").value("Dr. Ime"))
                .andExpect(jsonPath("$[0].prezime").value("Prezime"))
                .andExpect(jsonPath("$[0].email").value("ime.prezime@domen.com"));
    }

}
*/