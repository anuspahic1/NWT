package com.medicinska.rezervacija.obavijesti_dokumentacija.config;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DokumentacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.NotifikacijaDTO; // Dodano
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.OdgovorNaPorukuDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PorukaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Dokumentacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija; // Dodano
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.OdgovorNaPoruku;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Poruka;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.modelmapper.spi.MappingContext;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.LocalDateTime; // Dodano
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true);

        Converter<Notifikacija.Uloga, String> ulogaToStringConverter = new Converter<Notifikacija.Uloga, String>() {
            @Override
            public String convert(MappingContext<Notifikacija.Uloga, String> context) {
                return context.getSource() != null ? context.getSource().name() : null;
            }
        };

        Converter<LocalDateTime, String> localDateTimeToStringConverter = new Converter<LocalDateTime, String>() {
            @Override
            public String convert(MappingContext<LocalDateTime, String> context) {
                // Pretvara LocalDateTime u ISO-8601 string, koji je pogodan za frontend
                return context.getSource() != null ? context.getSource().toString() : null;
            }
        };

        Converter<Notifikacija.StatusNotifikacije, String> statusNotifikacijeToStringConverter = new Converter<Notifikacija.StatusNotifikacije, String>() {
            @Override
            public String convert(MappingContext<Notifikacija.StatusNotifikacije, String> context) {
                return context.getSource() != null ? context.getSource().name() : null;
            }
        };


        modelMapper.createTypeMap(OdgovorNaPoruku.class, OdgovorNaPorukuDTO.class)
                .addMappings(mapper -> {
                    mapper.using(ulogaToStringConverter).map(OdgovorNaPoruku::getSenderType, OdgovorNaPorukuDTO::setSenderType);
                    mapper.skip(OdgovorNaPorukuDTO::setParentMessageId);
                });

        TypeMap<Poruka, PorukaDTO> porukaToDtoTypeMap = modelMapper.createTypeMap(Poruka.class, PorukaDTO.class);
        porukaToDtoTypeMap.addMappings(mapper -> {
            mapper.using(ulogaToStringConverter).map(Poruka::getSenderType, PorukaDTO::setSenderType);
            mapper.using(ulogaToStringConverter).map(Poruka::getReceiverType, PorukaDTO::setReceiverType);

            mapper.<List<OdgovorNaPorukuDTO>>map(src -> {
                if (src.getReplies() == null) {
                    return Collections.emptyList();
                }
                return src.getReplies().stream()
                        .map(reply -> modelMapper.map(reply, OdgovorNaPorukuDTO.class))
                        .collect(Collectors.toList());
            }, PorukaDTO::setReplies);
        });

        TypeMap<Notifikacija, NotifikacijaDTO> notifikacijaToDtoTypeMap = modelMapper.createTypeMap(Notifikacija.class, NotifikacijaDTO.class);
        notifikacijaToDtoTypeMap.addMappings(mapper -> {
            mapper.using(statusNotifikacijeToStringConverter).map(Notifikacija::getStatus, NotifikacijaDTO::setStatus);
            mapper.using(localDateTimeToStringConverter).map(Notifikacija::getDatumSlanja, NotifikacijaDTO::setDatumSlanja);
            mapper.using(ulogaToStringConverter).map(Notifikacija::getUloga, NotifikacijaDTO::setUloga);
            });


        modelMapper.createTypeMap(Dokumentacija.class, DokumentacijaDTO.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getPristup() != null ? src.getPristup().name() : null, DokumentacijaDTO::setPristup);
                    mapper.skip(DokumentacijaDTO::setSadrzajDokumenta);
                });

        return modelMapper;
    }
}
