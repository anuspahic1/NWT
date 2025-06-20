package com.medicinska.rezervacija.korisnici_doktori.service;

import com.medicinska.rezervacija.korisnici_doktori.dto.DoktorDTO;
import com.medicinska.rezervacija.korisnici_doktori.exception.ResourceNotFoundException;
import com.medicinska.rezervacija.korisnici_doktori.mapper.DoktorMapper;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import com.medicinska.rezervacija.korisnici_doktori.model.OcjenaDoktora; // DODAN IMPORT
import com.medicinska.rezervacija.korisnici_doktori.repository.DoktorRepository;
import com.medicinska.rezervacija.korisnici_doktori.repository.OcjenaDoktoraRepository; // DODAN IMPORT
import com.medicinska.rezervacija.korisnici_doktori.remote.TerminiPreglediRemoteService; // Održavamo ovaj import jer ga DoktorService inače koristi
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@Transactional // Ova anotacija na nivou klase primjenjuje se na sve javne metode,
// ali @Transactional na metodi ima prednost ako je specifična.
@RequiredArgsConstructor // Generira konstruktor za final polja
public class DoktorService {
    private final DoktorRepository doktorRepository;
    private final ModelMapper modelMapper;
    private final DoktorMapper doktorMapper;
    private final TerminiPreglediRemoteService terminiPreglediRemoteService;
    private final OcjenaDoktoraRepository ocjenaDoktoraRepository; // KLJUČNO: DODAN REPOZITORIJ ZA OCJENE

    @PostConstruct
    public void configureModelMapper() {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);
    }

    public Page<DoktorDTO> findAllPaginated(int page, int size, String[] sort) {
        List<Sort.Order> orders = new ArrayList<>();

        if (sort[0].contains(",")) {
            for (String sortOrder : sort) {
                String[] _sort = sortOrder.split(",");
                orders.add(new Sort.Order(getSortDirection(_sort[1]), _sort[0]));
            }
        } else {
            orders.add(new Sort.Order(getSortDirection(sort[1]), sort[0]));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));
        Page<Doktor> doktoriPage = doktorRepository.findAll(pageable);

        return doktoriPage.map(this::convertToDto);
    }

    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    public List<DoktorDTO> findTopRatedDoctors(double minOcjena, int minIskustvo) {
        return doktorRepository.findTopRatedDoctors(minOcjena, minIskustvo)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> findByNumberOfSpecializations(int minSpecijalizacija) {
        return doktorRepository.findByNumberOfSpecializations(minSpecijalizacija)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> getAllDoktori() {
        return doktorRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private DoktorDTO convertToDto(Doktor doktor) {
        DoktorDTO dto = doktorMapper.toDto(doktor);
        dto.setProfileImageBase64(doktor.getProfileImageBase64());
        return dto;
    }


    public DoktorDTO findById(Integer id) {
        Doktor doktor = doktorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor nije pronađen"));
        DoktorDTO dto = doktorMapper.toDto(doktor);
        dto.setProfileImageBase64(doktor.getProfileImageBase64());
        return dto;
    }

    public DoktorDTO getDoktorByUserId(Long userId) {
        Doktor doktor = doktorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor sa User ID-om " + userId + " nije pronađen."));
        return convertToDto(doktor);
    }


    public DoktorDTO save(DoktorDTO doktorDTO) {
        Doktor doktor = doktorMapper.toEntity(doktorDTO);
        doktor.setProfileImageBase64(doktorDTO.getProfileImageBase64());
        Doktor savedDoktor = doktorRepository.save(doktor);
        return convertToDto(savedDoktor);
    }

    @Transactional
    public void updateDoktorOcjena(Integer doktorId, Double newOcjena) {
        Doktor doktor = doktorRepository.findById(doktorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor sa ID-em " + doktorId + " nije pronađen za ažuriranje ocjene."));
        doktor.setOcjena(newOcjena);
        doktorRepository.save(doktor);
    }

    // ISPRAVLJENA METODA: Sada dohvaća ocjene iz OcjenaDoktoraRepository
    @Transactional
    public void recalculateAndSaveAverageRating(Integer doktorId) {
        System.out.println("DEBUG (DoktorService): Pokrećem 'recalculateAndSaveAverageRating' za doktora ID: " + doktorId);

        Doktor doktor = doktorRepository.findById(doktorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor sa ID-em " + doktorId + " nije pronađen za preračunavanje ocjene."));

        // KLJUČNA IZMJENA: Dohvati ocjene direktno iz OcjenaDoktoraRepository
        List<OcjenaDoktora> ocjene = ocjenaDoktoraRepository.findByDoktor_DoktorID(doktorId);
        System.out.println("DEBUG (DoktorService): Pronađeno " + ocjene.size() + " ocjena za doktora ID: " + doktorId);


        if (ocjene != null && !ocjene.isEmpty()) {
            OptionalDouble average = ocjene.stream()
                    .filter(o -> o.getOcjena() != null) // Provjerite da vrijednost ocjene nije null
                    .mapToDouble(OcjenaDoktora::getOcjena) // Dobijte Double vrijednost iz OcjenaDoktora objekta
                    .average();
            if (average.isPresent()) {
                doktor.setOcjena(average.getAsDouble());
            } else {
                doktor.setOcjena(0.0); // Ako stream vrati prazan Optional (npr. svi null ocjene)
            }
        } else {
            doktor.setOcjena(0.0); // Ako nema ocjena, postavite na 0
        }
        doktorRepository.save(doktor);
        System.out.println("DEBUG (DoktorService): Prosječna ocjena za doktora ID " + doktorId + " preračunata na: " + doktor.getOcjena());
    }


    public List<DoktorDTO> findByGrad(String grad) {
        return doktorRepository.findByGrad(grad)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> findBySpecijalizacija(String specijalizacija) {
        return doktorRepository.findBySpecijalizacijaContains(specijalizacija)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public DoktorDTO update(Integer id, DoktorDTO doktorDTO) {
        Doktor existingDoktor = doktorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doktor nije pronađen"));

        modelMapper.map(doktorDTO, existingDoktor);
        existingDoktor.setProfileImageBase64(doktorDTO.getProfileImageBase64());
        if (doktorDTO.getSpecijalizacije() != null) {
            existingDoktor.setSpecijalizacije(new ArrayList<>(doktorDTO.getSpecijalizacije()));
        }


        Doktor updatedDoktor = doktorRepository.save(existingDoktor);
        return convertToDto(updatedDoktor);
    }

    public void delete(Integer id) {
        if (!doktorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doktor nije pronađen");
        }
        doktorRepository.deleteById(id);
    }

    public Page<DoktorDTO> getAllDoktoriPaged(Pageable pageable) {
        return doktorRepository.findAll(pageable)
                .map(this::convertToDto);
    }

    public List<DoktorDTO> getDoktoriBySpecijalizacija(String nazivSpecijalizacije) {
        return doktorRepository.findBySpecijalizacijaContains(nazivSpecijalizacije)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> saveAll(List<DoktorDTO> doktorDTOs) {
        List<Doktor> doktori = doktorDTOs.stream()
                .map(doktorMapper::toEntity)
                .collect(Collectors.toList());

        for (int i = 0; i < doktori.size(); i++) {
            doktori.get(i).setProfileImageBase64(doktorDTOs.get(i).getProfileImageBase64());
        }

        List<Doktor> saved = doktorRepository.saveAll(doktori);
        if (saved.isEmpty()) {
            throw new RuntimeException("Nema doktora koji su spremljeni.");
        }

        return saved.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> getDoktoriByImeAndPrezime(String ime, String prezime) {
        return doktorRepository.findByImeAndPrezime(ime, prezime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> searchDoktoriByImePrezime(String ime, String prezime) {
        return doktorRepository.findByImeContainingIgnoreCaseAndPrezimeContainingIgnoreCase(ime, prezime)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> getDoktoriByGradAndSpecijalizacija(String grad, String specijalizacija) {
        return doktorRepository.findByGradAndSpecijalizacija(grad, specijalizacija)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<DoktorDTO> getTopDoktori(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("ocjena").descending());
        return doktorRepository.findTopNDoktori(pageable)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getDoctorStatistics() {
        return doktorRepository.getDoctorStatsByCity().stream()
                .map(stats -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("grad", stats.getGrad());
                    map.put("brojDoktora", stats.getBrojDoktora());
                    map.put("prosjecnaOcjena", stats.getProsjecnaOcjena());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
