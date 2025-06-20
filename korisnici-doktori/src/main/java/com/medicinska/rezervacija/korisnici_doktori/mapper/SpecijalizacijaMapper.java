package com.medicinska.rezervacija.korisnici_doktori.mapper;

import com.medicinska.rezervacija.korisnici_doktori.dto.SpecijalizacijaDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Specijalizacija;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface SpecijalizacijaMapper {

    @Mapping(source = "nazivSpecijalizacije", target = "naziv")
    SpecijalizacijaDTO toDto(Specijalizacija specijalizacija);

    @Mapping(source = "naziv", target = "nazivSpecijalizacije")
    Specijalizacija toEntity(SpecijalizacijaDTO specijalizacijaDTO);

}