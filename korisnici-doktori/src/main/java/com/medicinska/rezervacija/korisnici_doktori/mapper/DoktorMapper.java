package com.medicinska.rezervacija.korisnici_doktori.mapper;

import com.medicinska.rezervacija.korisnici_doktori.dto.DoktorDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.Doktor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DoktorMapper {

    @Mapping(target = "doktorID", source = "doktorID")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "ime", source = "ime")
    @Mapping(target = "prezime", source = "prezime")
    @Mapping(target = "grad", source = "grad")
    @Mapping(target = "radnoVrijeme", source = "radnoVrijeme")
    @Mapping(target = "iskustvo", source = "iskustvo")
    @Mapping(target = "ocjena", source = "ocjena")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "telefon", source= "telefon")
    @Mapping(target = "specijalizacije", source = "specijalizacije")
    @Mapping(target = "profileImageBase64", source = "profileImageBase64")
    DoktorDTO toDto(Doktor doktor);

    @Mapping(target = "doktorID", ignore = true)
    @Mapping(target = "profileImageBase64", source = "profileImageBase64")
    Doktor toEntity(DoktorDTO doktorDTO);

    @Mapping(target = "doktorID", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "profileImageBase64", source = "profileImageBase64")
    void updateDoktorFromDto(DoktorDTO doktorDTO, @MappingTarget Doktor doktor);
}
