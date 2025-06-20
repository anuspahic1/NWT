package com.medicinska.rezervacija.korisnici_doktori.service;

import com.medicinska.rezervacija.korisnici_doktori.dto.SpecijalizacijaDTO;
import com.medicinska.rezervacija.korisnici_doktori.mapper.DoktorMapper;
import com.medicinska.rezervacija.korisnici_doktori.mapper.SpecijalizacijaMapper;
import com.medicinska.rezervacija.korisnici_doktori.model.Specijalizacija;
import com.medicinska.rezervacija.korisnici_doktori.repository.SpecijalizacijaRepository;
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
public class SpecijalizacijaService {
    private final SpecijalizacijaRepository specijalizacijaRepository;
    private final SpecijalizacijaMapper specijalizacijaMapper;
    private final DoktorMapper doktorMapper;

    public List<SpecijalizacijaDTO> findAll() {
        return specijalizacijaRepository.findAll()
                .stream()
                .map(specijalizacijaMapper::toDto)
                .collect(Collectors.toList());
    }
    private Sort.Direction getSortDirection(String direction) {
        if (direction.equals("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }
    public Page<SpecijalizacijaDTO> findAllPaginated(int page, int size, String[] sort) {
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
        return specijalizacijaRepository.findAll(pageable)
                .map(specijalizacijaMapper::toDto);
    }
    public SpecijalizacijaDTO save(SpecijalizacijaDTO specijalizacijaDTO) {
        Specijalizacija specijalizacija = specijalizacijaMapper.toEntity(specijalizacijaDTO);
        Specijalizacija savedSpec = specijalizacijaRepository.save(specijalizacija);
        return specijalizacijaMapper.toDto(savedSpec);
    }



    public Specijalizacija save(Specijalizacija specijalizacija) {
        return specijalizacijaRepository.save(specijalizacija);
    }
}