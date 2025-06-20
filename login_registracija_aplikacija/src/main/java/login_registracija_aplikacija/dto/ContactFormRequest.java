package login_registracija_aplikacija.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactFormRequest {
    @NotBlank(message = "Ime je obavezno.")
    private String name;

    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Neispravan format email adrese.")
    private String email;

    @NotBlank(message = "Naslov je obavezan.")
    private String subject;

    @NotBlank(message = "Poruka je obavezna.")
    private String message;
}
