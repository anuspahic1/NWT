package com.medicinska.rezervacija.korisnici_doktori.service;

import com.medicinska.rezervacija.korisnici_doktori.dto.PacijentDTO;
import com.medicinska.rezervacija.korisnici_doktori.dto.PacijentNotesDTO;
import com.medicinska.rezervacija.korisnici_doktori.exception.ResourceNotFoundException;
import com.medicinska.rezervacija.korisnici_doktori.mapper.PacijentMapper;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import com.medicinska.rezervacija.korisnici_doktori.repository.PacijentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PacijentService {

    private final PacijentRepository pacijentRepository;
    private final PacijentMapper pacijentMapper;


    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    public List<PacijentDTO> findActivePatients(int minOcjena) {
        return pacijentRepository.findActivePatients(minOcjena)
                .stream()
                .map(pacijentMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<PacijentDTO> findAllPaginated(int page, int size, String[] sort) {
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
        return pacijentRepository.findAll(pageable)
                .map(pacijentMapper::toDto);
    }

    public List<PacijentDTO> findAll() {
        return pacijentMapper.toDtoList(pacijentRepository.findAll());
    }

    public PacijentDTO findById(Integer id) {
        return pacijentRepository.findById(id)
                .map(pacijentMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Pacijent nije pronađen"));
    }

    public PacijentDTO getPacijentByUserId(Long userId) {
        Pacijent pacijent = pacijentRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Pacijent sa User ID-om " + userId + " nije pronađen."));
        return pacijentMapper.toDto(pacijent);
    }


    public PacijentDTO save(PacijentDTO pacijentDTO) {
        if (pacijentDTO.getUserId() != null && pacijentRepository.findByUserId(pacijentDTO.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Pacijent sa istim korisničkim ID-em već postoji.");
        }
        if (pacijentDTO.getEmail() != null && pacijentRepository.findByEmail(pacijentDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Pacijent sa istim emailom već postoji.");
        }

        Pacijent pacijent = pacijentMapper.toEntity(pacijentDTO);
        pacijent.setPacijentID(null);
        if (pacijent.getNotes() == null) {
            pacijent.setNotes("");
        }
        Pacijent savedPacijent = pacijentRepository.save(pacijent);
        return pacijentMapper.toDto(savedPacijent);
    }

    public PacijentDTO update(Integer id, PacijentDTO pacijentDTO) {
        return pacijentRepository.findById(id)
                .map(existingPacijent -> {
                    pacijentMapper.updateEntityFromDto(pacijentDTO, existingPacijent);

                    if (pacijentDTO.getEmail() != null && !pacijentDTO.getEmail().equals(existingPacijent.getEmail()) &&
                            pacijentRepository.findByEmail(pacijentDTO.getEmail()).isPresent() &&
                            !pacijentRepository.findByEmail(pacijentDTO.getEmail()).get().getPacijentID().equals(id)) {
                        throw new IllegalArgumentException("Email adresa već postoji za drugog pacijenta.");
                    }
                    if (pacijentDTO.getUserId() != null && !pacijentDTO.getUserId().equals(existingPacijent.getUserId()) &&
                            pacijentRepository.findByUserId(pacijentDTO.getUserId()).isPresent() &&
                            !pacijentRepository.findByUserId(pacijentDTO.getUserId()).get().getPacijentID().equals(id)) {
                        throw new IllegalArgumentException("Korisnički ID već postoji za drugog pacijenta.");
                    }

                    Pacijent updatedPacijent = pacijentRepository.save(existingPacijent);
                    return pacijentMapper.toDto(updatedPacijent);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Pacijent nije pronađen"));
    }

    public void delete(Integer id) {
        Pacijent pacijent = pacijentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pacijent nije pronađen"));
        pacijentRepository.delete(pacijent);
    }

    public PacijentNotesDTO getPacijentNotes(Integer pacijentId) {
        Pacijent pacijent = pacijentRepository.findById(pacijentId)
                .orElseThrow(() -> new EntityNotFoundException("Pacijent sa ID-om " + pacijentId + " nije pronađen."));
        return PacijentNotesDTO.builder()
                .notes(pacijent.getNotes() != null ? pacijent.getNotes() : "")
                .build();
    }

    @Transactional
    public void updatePacijentNotes(Integer pacijentId, PacijentNotesDTO notesDTO) {
        Pacijent pacijent = pacijentRepository.findById(pacijentId)
                .orElseThrow(() -> new EntityNotFoundException("Pacijent sa ID-om " + pacijentId + " nije pronađen."));
        pacijent.setNotes(notesDTO.getNotes());
        pacijentRepository.save(pacijent);
    }
}
