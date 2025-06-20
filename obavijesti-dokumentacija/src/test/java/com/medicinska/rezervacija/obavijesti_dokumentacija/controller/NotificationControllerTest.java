/*package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.NotifikacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class NotificationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSendNotification() throws Exception {
        // Kreiramo DTO koji ćemo poslati u POST zahtev
        NotifikacijaDTO notifikacijaDTO = new NotifikacijaDTO();
        notifikacijaDTO.setNotifikacijaID(1L);
        notifikacijaDTO.setSadrzaj("Test message");
        notifikacijaDTO.setStatus("PENDING");
        notifikacijaDTO.setDatumSlanja("2025-04-01");
        notifikacijaDTO.setKorisnikID(100L);

        // Mockovanje servisa da vrati isti DTO nakon slanja notifikacije
        when(notificationService.sendNotification(100L, "Test message")).thenReturn(notifikacijaDTO);

        // Slanje POST zahteva i provera odgovora
        mockMvc.perform(post("/notifikacije/send")
                        .param("korisnikID", "100")
                        .param("message", "Test message")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())  // Očekujemo status 200 OK
                .andExpect(jsonPath("$.notifikacijaID").value(1))
                .andExpect(jsonPath("$.sadrzaj").value("Test message"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.datumSlanja").value("2025-04-01"))
                .andExpect(jsonPath("$.korisnikID").value(100));
    }

    @Test
    public void testGetNotificationById() throws Exception {
        // Kreiramo DTO koji ćemo koristiti za povratak notifikacije
        NotifikacijaDTO notifikacijaDTO = new NotifikacijaDTO();
        notifikacijaDTO.setNotifikacijaID(1L);
        notifikacijaDTO.setSadrzaj("Test message");
        notifikacijaDTO.setStatus("SENT");
        notifikacijaDTO.setDatumSlanja("2025-04-01");
        notifikacijaDTO.setKorisnikID(100L);

        // Mockovanje servisa da vrati DTO za zadati ID
        when(notificationService.getNotificationById(1L)).thenReturn(notifikacijaDTO);

        // Slanje GET zahteva i provera odgovora
        mockMvc.perform(get("/notifikacije/1"))
                .andExpect(status().isOk())  // Očekujemo status 200 OK
                .andExpect(jsonPath("$.notifikacijaID").value(1))
                .andExpect(jsonPath("$.sadrzaj").value("Test message"))
                .andExpect(jsonPath("$.status").value("SENT"))
                .andExpect(jsonPath("$.datumSlanja").value("2025-04-01"))
                .andExpect(jsonPath("$.korisnikID").value(100));
    }
}
*/