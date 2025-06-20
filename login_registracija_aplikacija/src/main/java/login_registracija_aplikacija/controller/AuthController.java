package login_registracija_aplikacija.controller;

import login_registracija_aplikacija.dto.AuthResponse;
import login_registracija_aplikacija.dto.LoginRequest;
import login_registracija_aplikacija.dto.RegisterRequest;
import login_registracija_aplikacija.service.AuthService;
import login_registracija_aplikacija.dto.ResetPasswordRequest;
import login_registracija_aplikacija.dto.ForgotPasswordRequest;
import login_registracija_aplikacija.dto.ContactFormRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        System.out.println("Primljen login request: " + request);
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forgot-password-request")
    public ResponseEntity<String> forgotPasswordRequest( @RequestBody ForgotPasswordRequest request) {
        authService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok("Zahtjev za reset lozinke uspješno poslan na " + request.getEmail());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword( @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Lozinka uspješno resetovana.");
    }

    @PostMapping("/contact")
    public ResponseEntity<String> handleContactForm( @RequestBody ContactFormRequest request) {
        authService.sendContactForm(request);
        return ResponseEntity.ok("Vaša poruka je uspješno poslana.");
    }
}
