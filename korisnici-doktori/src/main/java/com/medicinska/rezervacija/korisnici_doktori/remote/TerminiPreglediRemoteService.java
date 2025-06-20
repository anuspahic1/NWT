package com.medicinska.rezervacija.korisnici_doktori.remote;

import com.medicinska.rezervacija.korisnici_doktori.dto.PregledDTO;
import java.util.List;

public interface TerminiPreglediRemoteService {

    List<Double> getDoctorRatings(Integer doktorId);

    PregledDTO getPregledById(Integer id); // <-- NOVO

    PregledDTO updatePregled(Integer id, PregledDTO pregledDTO);
}
