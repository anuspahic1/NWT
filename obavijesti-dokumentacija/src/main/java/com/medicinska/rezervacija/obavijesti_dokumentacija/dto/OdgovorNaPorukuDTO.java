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

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OdgovorNaPorukuDTO {
    private Long odgovorID;
    private String senderIme;

    @NotNull(message = "ID glavne poruke je obavezan")
    private Long parentMessageId;

    @NotNull(message = "ID pošiljatelja odgovora je obavezan")
    private Long senderId;

    @NotBlank(message = "Tip pošiljatelja odgovora je obavezan")
    private String senderType;

    @NotBlank(message = "Sadržaj odgovora je obavezan")
    @Size(max = 2000, message = "Sadržaj odgovora ne može biti duži od 2000 karaktera")
    private String content;

    private LocalDateTime timestamp;
}