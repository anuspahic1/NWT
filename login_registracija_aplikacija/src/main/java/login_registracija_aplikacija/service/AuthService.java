package login_registracija_aplikacija.service;

import login_registracija_aplikacija.dto.AuthResponse;
import login_registracija_aplikacija.dto.ContactFormRequest;
import login_registracija_aplikacija.dto.LoginRequest;
import login_registracija_aplikacija.dto.RegisterRequest;
import login_registracija_aplikacija.model.User;
import login_registracija_aplikacija.repository.UserRepository;
import login_registracija_aplikacija.dto.UserRegisteredEventDTO;
import login_registracija_aplikacija.dto.UserUpdateDTO;
import login_registracija_aplikacija.dto.DoktorDTO;
import login_registracija_aplikacija.dto.PacijentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.Set;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RestTemplate restTemplate;

    private static final String KORISNICI_DOKTORI_SERVICE_ID = "http://KORISNICI-DOKTORI";
    private static final String USER_SYNC_PATH = "/korisnici-doktori/api/internal/user-sync/registered";

    @Value("${application.contact.email:info@winxcare.com}")
    private String contactRecipientEmail;

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty() ||
                request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Email, password, and full name are required.");
        }
        if (request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!request.getEmail().matches("\\S+@\\S+\\.\\S+")) {
            throw new IllegalArgumentException("Email address is not valid.");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists!");
        }

        Set<String> rolesToAssign = request.getRoles() != null && !request.getRoles().isEmpty() ? request.getRoles() : Collections.singleton("ROLE_PACIJENT");

        User newUser = User.builder()
                .username(request.getEmail())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .roles(rolesToAssign)
                .telefon(request.getTelefon())
                .grad(request.getGrad())
                .specijalizacije(request.getSpecijalizacije())
                .ocjena(request.getOcjena())
                .iskustvo(request.getIskustvo())
                .radnoVrijeme(request.getRadnoVrijeme())
                .build();
        User savedUser = userRepository.save(newUser);
        System.out.println("User registered in Auth service: " + savedUser.getEmail() + " with ID: " + savedUser.getId());

        try {
            String ime = "";
            String prezime = "";
            if (savedUser.getFullName() != null && !savedUser.getFullName().isEmpty()) {
                String[] nameParts = savedUser.getFullName().split(" ", 2);
                ime = nameParts[0];
                if (nameParts.length > 1) {
                    prezime = nameParts[1];
                }
            }

            UserRegisteredEventDTO eventDTO = UserRegisteredEventDTO.builder()
                    .userId(savedUser.getId())
                    .email(savedUser.getEmail())
                    .ime(ime)
                    .prezime(prezime)
                    .telefon(savedUser.getTelefon())
                    .roles(savedUser.getRoles())
                    .grad(savedUser.getGrad())
                    .specijalizacije(savedUser.getSpecijalizacije())
                    .ocjena(savedUser.getOcjena())
                    .iskustvo(savedUser.getIskustvo())
                    .radnoVrijeme(savedUser.getRadnoVrijeme())
                    .build();

            String url = KORISNICI_DOKTORI_SERVICE_ID + USER_SYNC_PATH;
            System.out.println("Attempting to send UserRegisteredEvent to: " + url + " with payload: " + eventDTO.toString());
            restTemplate.postForEntity(url, eventDTO, Void.class);
            System.out.println("Successfully sent UserRegisteredEvent for userId: " + eventDTO.getUserId());
        } catch (HttpClientErrorException e) {
            System.err.println("HTTP error sending UserRegisteredEvent: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("Error sending UserRegisteredEvent to Korisnici-Doktori service: " + e.getMessage());
        }

        var jwtToken = jwtService.generateToken(savedUser);
        return AuthResponse.builder()
                .token(jwtToken)
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .roles(savedUser.getRoles())
                .userId(savedUser.getId())
                .message("Registration successful!")
                .grad(savedUser.getGrad())
                .specijalizacije(savedUser.getSpecijalizacije())
                .ocjena(savedUser.getOcjena())
                .iskustvo(savedUser.getIskustvo())
                .radnoVrijeme(savedUser.getRadnoVrijeme())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
                request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Email and password are required.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            System.err.println("Login failed for user " + request.getEmail() + ": Incorrect email or password.");
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("Incorrect email or password.");
        } catch (AuthenticationException e) {
            System.err.println("Authentication FAILED for user " + request.getEmail() + ": " + e.getMessage());
            throw new org.springframework.security.authentication.InsufficientAuthenticationException("Authentication failed.");
        } catch (Exception e) {
            System.err.println("Unexpected error during login for user " + request.getEmail() + ": " + e.getMessage());
            throw new RuntimeException("An error occurred during login. Please try again.", e);
        }


        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found."));

        var jwtToken = jwtService.generateToken(user);

        Integer doktorId = null;
        Integer pacijentId = null;

        if (user.getRoles().contains("ROLE_DOKTOR")) {
            try {
                String url = KORISNICI_DOKTORI_SERVICE_ID + "/korisnici-doktori/api/doktori/by-auth-user/" + user.getId();
                System.out.println("Attempting to fetch DoktorDTO from: " + url + " for userId: " + user.getId());
                DoktorDTO doktor = restTemplate.getForObject(url, DoktorDTO.class);
                if (doktor != null) {
                    doktorId = doktor.getDoktorID();
                    System.out.println("Fetched DoktorID: " + doktorId + " for userId: " + user.getId());
                } else {
                    System.err.println("DoktorDTO is null for userId: " + user.getId());
                }
            } catch (HttpClientErrorException.NotFound ex) {
                System.err.println("Doctor profile not found (404) for userId: " + user.getId() + ". Error: " + ex.getMessage());
            } catch (Exception e) {
                System.err.println("Error calling Korisnici-Doktori service for doctor: " + e.getMessage());
            }
        } else if (user.getRoles().contains("ROLE_PACIJENT")) {
            try {
                String url = KORISNICI_DOKTORI_SERVICE_ID + "/korisnici-doktori/api/pacijenti/by-auth-user/" + user.getId();
                System.out.println("Attempting to fetch PacijentDTO from: " + url + " for userId: " + user.getId());
                PacijentDTO pacijent = restTemplate.getForObject(url, PacijentDTO.class);
                if (pacijent != null) {
                    pacijentId = pacijent.getPacijentID();
                    System.out.println("Fetched PacijentID: " + pacijentId + " for userId: " + user.getId());
                } else {
                    System.err.println("PacijentDTO is null for userId: " + user.getId());
                }
            } catch (HttpClientErrorException.NotFound ex) {
                System.err.println("Patient profile not found (404) for userId: " + user.getId() + ". Error: " + ex.getMessage());
            } catch (Exception e) {
                System.err.println("Error calling Korisnici-Doktori service for patient: " + e.getMessage());
            }
        }

        return AuthResponse.builder()
                .token(jwtToken)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .userId(user.getId())
                .doktorID(doktorId)
                .pacijentID(pacijentId)
                .message("Login successful!")
                .grad(user.getGrad())
                .specijalizacije(user.getSpecijalizacije())
                .ocjena(user.getOcjena())
                .iskustvo(user.getIskustvo())
                .radnoVrijeme(user.getRadnoVrijeme())
                .build();
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with this email not found."));

        String resetToken = UUID.randomUUID().toString();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiryDate(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;

        String subject = "WinxCare Password Reset";
        String text = "Dear User,\\n\\n"
                + "We received a request to reset the password for your WinxCare account.\\n"
                + "Click on the link below to reset your password:\\n"
                + resetLink + "\\n\\n"
                + "This link will expire in 24 hours.\\n"
                + "If you did not send this request, please ignore this message.\\n\\n"
                + "Sincerely,\\n"
                + "WinxCare Team";
        emailService.sendEmail(user.getEmail(), subject, text);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or non-existent password reset token."));

        if (user.getResetPasswordTokenExpiryDate() == null || user.getResetPasswordTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Password reset token has expired.");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiryDate(null);
        userRepository.save(user);
    }

    public void sendContactForm(ContactFormRequest request) {
        emailService.sendContactFormEmail(
                request.getName(),
                request.getEmail(),
                request.getSubject(),
                request.getMessage(),
                contactRecipientEmail
        );
    }

    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found."));
        userRepository.delete(user);
        System.out.println("User with ID " + userId + " deleted from Auth database.");
    }

    public void updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + userId + " not found."));

        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().isEmpty()) {
            existingUser.setEmail(userUpdateDTO.getEmail());
            existingUser.setUsername(userUpdateDTO.getEmail());
        }
        if (userUpdateDTO.getFullName() != null && !userUpdateDTO.getFullName().isEmpty()) {
            existingUser.setFullName(userUpdateDTO.getFullName());
        }
        if (userUpdateDTO.getTelefon() != null && !userUpdateDTO.getTelefon().isEmpty()) {
            existingUser.setTelefon(userUpdateDTO.getTelefon());
        }
        if (userUpdateDTO.getRoles() != null && !userUpdateDTO.getRoles().isEmpty()) {
            existingUser.setRoles(userUpdateDTO.getRoles());
        }
        userRepository.save(existingUser);
        System.out.println("User with ID " + userId + " updated in Auth database.");
    }
}
