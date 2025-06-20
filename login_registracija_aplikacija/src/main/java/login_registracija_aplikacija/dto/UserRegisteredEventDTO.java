package login_registracija_aplikacija.dto;

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
public class UserRegisteredEventDTO {
    private Long userId;
    private String email;
    private String ime;
    private String prezime;
    private String telefon;
    private String grad;
    private List<String> specijalizacije;
    private Set<String> roles;
    private Double ocjena;
    private Integer iskustvo;
    private String radnoVrijeme;
}
