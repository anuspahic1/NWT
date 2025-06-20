package login_registracija_aplikacija.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @NotBlank(message = "Email je obavezan.")
    @Email(message = "Neispravan format email adrese.")
    private String email;

    @NotBlank(message = "Lozinka je obavezna.")
    @Size(min = 6, message = "Lozinka mora imati barem 6 karaktera.")
    private String password;

    @NotBlank(message = "Puno ime je obavezno.")
    private String fullName;

    private Set<String> roles;

    private String telefon;
    private String grad;
    private List<String> specijalizacije;
    private Double ocjena;
    private Integer iskustvo;
    private String radnoVrijeme;
}
