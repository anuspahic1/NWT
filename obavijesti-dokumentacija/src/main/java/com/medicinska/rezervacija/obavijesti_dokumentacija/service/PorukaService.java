package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.DoktorDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.OdgovorNaPorukuDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PacijentDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.PorukaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.OdgovorNaPoruku;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Poruka;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.OdgovorNaPorukuRepository;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.PorukaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PorukaService {

    @Autowired
    private PorukaRepository porukaRepository;

    @Autowired
    private OdgovorNaPorukuRepository odgovorNaPorukuRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    private final String KORISNICI_DOKTORI_BASE_URL = "http://KORISNICI-DOKTORI/korisnici-doktori/api";

    private String getKorisnikIme(Long userId, Notifikacija.Uloga uloga) {
        if (userId == null) return "N/A";

        try {
            if (uloga == Notifikacija.Uloga.PACIJENT) {
                PacijentDTO pacijent = restTemplate.getForObject(
                        KORISNICI_DOKTORI_BASE_URL + "/pacijenti/by-auth-user/" + userId,
                        PacijentDTO.class
                );
                return (pacijent != null) ? pacijent.getIme() + " " + pacijent.getPrezime() : "N/A";
            } else if (uloga == Notifikacija.Uloga.DOKTOR) {
                DoktorDTO doktor = restTemplate.getForObject(
                        KORISNICI_DOKTORI_BASE_URL + "/doktori/by-auth-user/" + userId,
                        DoktorDTO.class
                );
                return (doktor != null) ? doktor.getIme() + " " + doktor.getPrezime() : "N/A";
            }
        } catch (HttpClientErrorException.NotFound ex) {
            System.err.println("Korisnik sa USER ID-em " + userId + " i ulogom " + uloga + " nije pronađen (HTTP 404): " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Greška prilikom dohvaćanja imena korisnika (USER ID: " + userId + ", Uloga: " + uloga + "): " + ex.getMessage());
        }
        return "N/A";
    }

    private PorukaDTO toPorukaDTO(Poruka poruka) {
        if (poruka == null) return null;

        PorukaDTO dto = modelMapper.map(poruka, PorukaDTO.class);

        dto.setSenderIme(getKorisnikIme(poruka.getSenderId(), poruka.getSenderType()));
        dto.setReceiverIme(getKorisnikIme(poruka.getReceiverId(), poruka.getReceiverType()));

         if (dto.getReplies() != null) {
            dto.setReplies(dto.getReplies().stream()
                    .peek(replyDto -> replyDto.setSenderIme(getKorisnikIme(replyDto.getSenderId(), Notifikacija.Uloga.valueOf(replyDto.getSenderType()))))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

     private OdgovorNaPorukuDTO toOdgovorNaPorukuDTO(OdgovorNaPoruku odgovor) {
        if (odgovor == null) return null;
        OdgovorNaPorukuDTO dto = modelMapper.map(odgovor, OdgovorNaPorukuDTO.class);
        dto.setSenderIme(getKorisnikIme(odgovor.getSenderId(), odgovor.getSenderType()));
        return dto;
    }


    public List<PorukaDTO> getAllPoruke() {
        return porukaRepository.findAll().stream()
                .map(this::toPorukaDTO)
                .collect(Collectors.toList());
    }

    public Optional<PorukaDTO> getPorukaById(Long porukaID) {
        Optional<Poruka> porukaOptional = porukaRepository.findById(porukaID);
        return porukaOptional.map(this::toPorukaDTO);
    }

    @Transactional
    public PorukaDTO sendPoruka(PorukaDTO porukaDTO) {
        if ("N/A".equals(getKorisnikIme(porukaDTO.getSenderId(), Notifikacija.Uloga.valueOf(porukaDTO.getSenderType()))) ||
                "N/A".equals(getKorisnikIme(porukaDTO.getReceiverId(), Notifikacija.Uloga.valueOf(porukaDTO.getReceiverType())))) {
            String senderStatus = "N/A".equals(getKorisnikIme(porukaDTO.getSenderId(), Notifikacija.Uloga.valueOf(porukaDTO.getSenderType()))) ? "Pošiljatelj (ID: " + porukaDTO.getSenderId() + ") nije pronađen." : "";
            String receiverStatus = "N/A".equals(getKorisnikIme(porukaDTO.getReceiverId(), Notifikacija.Uloga.valueOf(porukaDTO.getReceiverType()))) ? "Primatelj (ID: " + porukaDTO.getReceiverId() + ") nije pronađen." : "";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validacija pošiljatelja/primatelja neuspješna: " + senderStatus + " " + receiverStatus);
        }

        Poruka poruka = modelMapper.map(porukaDTO, Poruka.class);
        poruka.setSenderType(Notifikacija.Uloga.valueOf(porukaDTO.getSenderType()));
        poruka.setReceiverType(Notifikacija.Uloga.valueOf(porukaDTO.getReceiverType()));
        poruka.setTimestamp(LocalDateTime.now());

        Poruka savedPoruka = porukaRepository.save(poruka);
        return toPorukaDTO(savedPoruka);
    }

    @Transactional
    public Optional<PorukaDTO> replyToPoruka(Long parentMessageId, OdgovorNaPorukuDTO odgovorDTO) {
        Poruka parentMessage = porukaRepository.findById(parentMessageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Glavna poruka s ID-em " + parentMessageId + " nije pronađena."));

        if ("N/A".equals(getKorisnikIme(odgovorDTO.getSenderId(), Notifikacija.Uloga.valueOf(odgovorDTO.getSenderType())))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pošiljatelj odgovora (ID: " + odgovorDTO.getSenderId() + ") ne postoji.");
        }

        OdgovorNaPoruku odgovor = OdgovorNaPoruku.builder()
                .parentMessage(parentMessage)
                .senderId(odgovorDTO.getSenderId())
                .senderType(Notifikacija.Uloga.valueOf(odgovorDTO.getSenderType()))
                .content(odgovorDTO.getContent())
                .timestamp(LocalDateTime.now())
                .build();

        parentMessage.getReplies().add(odgovor);

        Poruka updatedPoruka = porukaRepository.save(parentMessage);
        return Optional.of(toPorukaDTO(updatedPoruka));
    }


    public List<PorukaDTO> getReceivedMessages(Long receiverId, Notifikacija.Uloga receiverType) {
        return porukaRepository.findByReceiverIdAndReceiverTypeOrderByTimestampDesc(receiverId, receiverType).stream()
                .map(this::toPorukaDTO)
                .collect(Collectors.toList());
    }

    public List<PorukaDTO> getSentMessages(Long senderId, Notifikacija.Uloga senderType) {
        return porukaRepository.findBySenderIdAndSenderTypeOrderByTimestampDesc(senderId, senderType).stream()
                .map(this::toPorukaDTO)
                .collect(Collectors.toList());
    }

    public List<PorukaDTO> getConversation(Long userId, Notifikacija.Uloga userType) {
        List<Poruka> poruke = porukaRepository.findBySenderIdOrReceiverIdOrderByTimestampDesc(userId, userId);

        return poruke.stream()
                .map(this::toPorukaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean deletePoruka(Long porukaID) {
        if (porukaRepository.existsById(porukaID)) {
            porukaRepository.deleteById(porukaID);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<PorukaDTO> updatePoruka(Long porukaID, PorukaDTO updatedPorukaDTO) {
        return porukaRepository.findById(porukaID)
                .map(existingPoruka -> {
                    if (updatedPorukaDTO.getSubject() != null) {
                        existingPoruka.setSubject(updatedPorukaDTO.getSubject());
                    }
                    if (updatedPorukaDTO.getContent() != null) {
                        existingPoruka.setContent(updatedPorukaDTO.getContent());
                    }
                    Poruka savedPoruka = porukaRepository.save(existingPoruka);
                    return toPorukaDTO(savedPoruka);
                });
    }
}