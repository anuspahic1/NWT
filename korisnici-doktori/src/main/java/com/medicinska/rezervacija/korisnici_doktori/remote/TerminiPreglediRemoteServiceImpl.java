package com.medicinska.rezervacija.korisnici_doktori.remote;

import com.medicinska.rezervacija.korisnici_doktori.dto.PregledDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus; // DODAN IMPORT ZA HttpStatus
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;

@Service
public class TerminiPreglediRemoteServiceImpl implements TerminiPreglediRemoteService {

    private final RestTemplate restTemplate;

    private static final String TERMINI_PREGLEDI_BASE_URL = "http://TERMINI-PREGLEDI";
    private static final String PREGLEDI_API_PATH = "/api/pregledi";

    public TerminiPreglediRemoteServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Double> getDoctorRatings(Integer doktorId) {
        try {
            String url = TERMINI_PREGLEDI_BASE_URL + PREGLEDI_API_PATH + "/doktor/" + doktorId + "/ratings";

            ResponseEntity<List<Double>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Double>>() {}
            );

            // ISPRAVLJENO: Provjeravamo status kod za 204 (No Content)
            if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                System.out.println("Nema ocjena za doktora ID " + doktorId + " (204 No Content).");
                return Collections.emptyList();
            }

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return Collections.emptyList(); // Default prazna lista u slučaju drugih uspješnih statusa bez tijela
        } catch (Exception ex) { // Hvatamo općenitiji izuzetak jer 204 nije greška
            System.err.println("Greška prilikom dohvaćanja ocjena pregleda za doktora ID " + doktorId + ": " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public PregledDTO getPregledById(Integer id) {
        try {
            String url = TERMINI_PREGLEDI_BASE_URL + PREGLEDI_API_PATH + "/" + id;
            ResponseEntity<PregledDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    PregledDTO.class
            );
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return null;
        } catch (HttpClientErrorException.NotFound ex) {
            System.err.println("Pregled sa ID " + id + " nije pronađen u termini-pregledi servisu: " + ex.getMessage());
            return null;
        } catch (Exception ex) {
            System.err.println("Greška prilikom dohvaćanja pregleda ID " + id + " iz termini-pregledi servisa: " + ex.getMessage());
            return null;
        }
    }

    @Override
    public PregledDTO updatePregled(Integer id, PregledDTO pregledDTO) {
        try {
            String url = TERMINI_PREGLEDI_BASE_URL + PREGLEDI_API_PATH + "/" + id;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<PregledDTO> requestEntity = new HttpEntity<>(pregledDTO, headers);

            ResponseEntity<PregledDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    PregledDTO.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
            return null;
        } catch (HttpClientErrorException.NotFound ex) {
            System.err.println("Pregled sa ID " + id + " nije pronađen za ažuriranje u termini-pregledi servisu: " + ex.getMessage());
            return null;
        } catch (Exception ex) {
            System.err.println("Greška prilikom ažuriranja pregleda ID " + id + " u termini-pregledi servisu: " + ex.getMessage());
            return null;
        }
    }
}
