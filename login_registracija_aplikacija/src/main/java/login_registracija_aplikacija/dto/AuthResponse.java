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
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Set<String> roles;
    private Integer doktorID;
    private Integer pacijentID;
    private String message;
    private Long userId;
    private String grad;
    private List<String> specijalizacije;
    private Double ocjena;
    private Integer iskustvo;
    private String radnoVrijeme;
}
