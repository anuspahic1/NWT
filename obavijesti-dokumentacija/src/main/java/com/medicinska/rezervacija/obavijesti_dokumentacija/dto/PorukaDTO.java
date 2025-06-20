package com.medicinska.rezervacija.obavijesti_dokumentacija.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PorukaDTO {
    private Long porukaID;
    private String senderIme;
    private String receiverIme;

    @NotNull(message = "ID pošiljatelja je obavezan")
    private Long senderId;

    @NotBlank(message = "Tip pošiljatelja je obavezan")
    private String senderType;

    @NotNull(message = "ID primatelja je obavezan")
    private Long receiverId;

    @NotBlank(message = "Tip primatelja je obavezan")
    private String receiverType;

    @NotBlank(message = "Predmet poruke je obavezan")
    @Size(max = 255, message = "Predmet ne može biti duži od 255 karaktera")
    private String subject;

    @NotBlank(message = "Sadržaj poruke je obavezan")
    @Size(max = 2000, message = "Sadržaj ne može biti duži od 2000 karaktera")
    private String content;

    private LocalDateTime timestamp;
    private List<OdgovorNaPorukuDTO> replies;
}