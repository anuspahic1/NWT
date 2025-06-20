package com.medicinska.rezervacija.korisnici_doktori.mapper;

import com.medicinska.rezervacija.korisnici_doktori.dto.PacijentDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Pacijent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PacijentMapper {

    PacijentDTO toDto(Pacijent pacijent);

    @Mapping(target = "ocjene", ignore = true)
    Pacijent toEntity(PacijentDTO pacijentDTO);

    default List<PacijentDTO> toDtoList(List<Pacijent> pacijenti) {
        return pacijenti.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    default void updateEntityFromDto(PacijentDTO dto, Pacijent entity) {
        if (dto == null || entity == null) {
            return;
        }

        if (dto.getIme() != null) {
            entity.setIme(dto.getIme());
        }
        if (dto.getPrezime() != null) {
            entity.setPrezime(dto.getPrezime());
        }

        if (dto.getTelefon() != null) {
            entity.setTelefon(dto.getTelefon());
        }

    }
}
