package login_registracija_aplikacija.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; private String token;
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER) @JoinColumn(nullable = false, name = "user_id")
    private User user; private LocalDateTime expiryDate;
}