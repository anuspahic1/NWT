package com.medicinska.rezervacija.korisnici_doktori.mapper;

import com.medicinska.rezervacija.korisnici_doktori.dto.OcjenaDoktoraDTO;
import com.medicinska.rezervacija.korisnici_doktori.model.OcjenaDoktora;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy; // Dodano za unmappedTargetPolicy

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE) // Dodano unmappedTargetPolicy
public interface OcjenaDoktoraMapper {

    @Mapping(target = "ocjenaID", source = "ocjenaID")
    @Mapping(target = "pacijentID", source = "pacijent.pacijentID")
    @Mapping(target = "doktorID", source = "doktor.doktorID")
    @Mapping(target = "pregledID", source = "pregledID") // DODANO: Mapiranje pregledID
    // @Mapping(target = "pacijentImePrezime", expression = "java(ocjena.getPacijent().getIme() + \" \" + ocjena.getPacijent().getPrezime())") // Ako ovo polje postoji u DTO, ostavite
    // @Mapping(target = "doktorImePrezime", expression = "java(ocjena.getDoktor().getIme() + \" \" + ocjena.getDoktor().getPrezime())") // Ako ovo polje postoji u DTO, ostavite
    @Mapping(target = "ocjena", source = "ocjena")
        // UKLONJENO: @Mapping(target = "komentar", source = "komentar")
    OcjenaDoktoraDTO toDto(OcjenaDoktora ocjena);

    @Mapping(target = "ocjenaID", source = "ocjenaID")
    @Mapping(target = "pacijent", ignore = true)
    @Mapping(target = "doktor", ignore = true)
    @Mapping(target = "pregledID", source = "pregledID") // DODANO: Mapiranje pregledID
    @Mapping(target = "ocjena", source = "ocjena")
        // UKLONJENO: @Mapping(target = "komentar", source = "komentar")
    OcjenaDoktora toEntity(OcjenaDoktoraDTO dto);
}
