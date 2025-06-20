package login_registracija_aplikacija.config;

import login_registracija_aplikacija.dto.RegisterRequest;
import login_registracija_aplikacija.repository.UserRepository;
import login_registracija_aplikacija.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Configuration
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        return args -> {
            //inicijalizacija Admin korisnika
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                Set<String> adminRoles = new HashSet<>();
                adminRoles.add("ROLE_ADMIN");

                RegisterRequest adminRegisterRequest = RegisterRequest.builder()
                        .email("admin@example.com")
                        .password("admin1")
                        .fullName("Admin User")
                        .roles(adminRoles)
                        .telefon("+38761123456")
                        .grad("Sarajevo")
                        .build();

                authService.register(adminRegisterRequest);
                logger.info("Kreiran admin korisnik: {}", adminRegisterRequest.getEmail());
            } else {
                logger.info("Admin korisnik već postoji.");
            }

            //inicijalizacija Doktor korisnika
            if (userRepository.findByEmail("doctor@example.com").isEmpty()) {
                Set<String> doctorRoles = new HashSet<>();
                doctorRoles.add("ROLE_DOKTOR");

                RegisterRequest doctorRegisterRequest = RegisterRequest.builder()
                        .email("doctor@example.com")
                        .password("doctor1")
                        .fullName("Doctor User")
                        .roles(doctorRoles)
                        .telefon("+38762987654")
                        .grad("Zenica")
                        .specijalizacije(Arrays.asList("Kardiolog", "Neurolog"))
                        .ocjena(4.5)
                        .iskustvo(10)
                        .radnoVrijeme("Pon-Pet 08:00-16:00")
                        .build();

                authService.register(doctorRegisterRequest);
                logger.info("Kreiran doktor korisnik: {}", doctorRegisterRequest.getEmail());
            } else {
                logger.info("Doktor korisnik već postoji.");
            }

            //inicijalizacija Pacijent korisnika
            if (userRepository.findByEmail("patient@example.com").isEmpty()) {
                Set<String> patientRoles = new HashSet<>();
                patientRoles.add("ROLE_PACIJENT");

                RegisterRequest patientRegisterRequest = RegisterRequest.builder()
                        .email("patient@example.com")
                        .password("patient1")
                        .fullName("Patient User")
                        .roles(patientRoles)
                        .telefon("+38763112233")
                        .grad("Zenica")
                        .build();

                authService.register(patientRegisterRequest);
                logger.info("Kreiran pacijent korisnik: {}", patientRegisterRequest.getEmail());
            } else {
                logger.info("Pacijent korisnik već postoji.");
            }

            if (userRepository.findByEmail("dzenana.selimovic@gmail.com").isEmpty()) {
                Set<String> doctorRoles = new HashSet<>();
                doctorRoles.add("ROLE_DOKTOR");

                RegisterRequest doctorRegisterRequest = RegisterRequest.builder()
                        .email("dzenana.selimovic@gmail.com")
                        .password("etfdzenana")
                        .fullName("Dženana Selimović")
                        .roles(doctorRoles)
                        .telefon("+38762987654")
                        .grad("Tuzla")
                        .specijalizacije(Arrays.asList("Pedijatar"))
                        .ocjena(3.5)
                        .iskustvo(8)
                        .radnoVrijeme("Pon-Pet 08:00-17:00")
                        .build();

                authService.register(doctorRegisterRequest);
                logger.info("Kreiran doktor korisnik: {}", doctorRegisterRequest.getEmail());
            } else {
                logger.info("Doktor korisnik već postoji.");
            }

            if (userRepository.findByEmail("ilma.dzaferovic@gmail.com").isEmpty()) {
                Set<String> doctorRoles = new HashSet<>();
                doctorRoles.add("ROLE_DOKTOR");

                RegisterRequest doctorRegisterRequest = RegisterRequest.builder()
                        .email("ilma.dzaferovic@gmail.com")
                        .password("etfilma")
                        .fullName("Ilma Džaferović")
                        .roles(doctorRoles)
                        .telefon("+38762987654")
                        .grad("Sarajevo")
                        .specijalizacije(Arrays.asList("Psihijatar"))
                        .ocjena(3.5)
                        .iskustvo(8)
                        .radnoVrijeme("Pon-Pet 09:00-17:00")
                        .build();

                authService.register(doctorRegisterRequest);
                logger.info("Kreiran doktor korisnik: {}", doctorRegisterRequest.getEmail());
            } else {
                logger.info("Doktor korisnik već postoji.");
            }
            if (userRepository.findByEmail("husein.hasanovic@gmail.com").isEmpty()) {
                Set<String> patientRoles = new HashSet<>();
                patientRoles.add("ROLE_PACIJENT");

                RegisterRequest patientRegisterRequest = RegisterRequest.builder()
                        .email("husein.hasanovic@gmail.com")
                        .password("patient2")
                        .fullName("Husein Hasanović")
                        .roles(patientRoles)
                        .telefon("+38763112233")
                        .grad("Bihać")
                        .build();

                authService.register(patientRegisterRequest);
                logger.info("Kreiran pacijent korisnik: {}", patientRegisterRequest.getEmail());
            } else {
                logger.info("Pacijent korisnik već postoji.");
            }

        };
    }
}
