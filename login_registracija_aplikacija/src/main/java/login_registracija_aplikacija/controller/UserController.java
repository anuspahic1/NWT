package login_registracija_aplikacija.controller;

import login_registracija_aplikacija.dto.UserUpdateDTO;
import login_registracija_aplikacija.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final AuthService authService;

    //endpoint za brisanje korisnika po userId
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    //endpoint za ažuriranje osnovnih korisničkih podataka po userId
    @PutMapping("/{userId}")
    public ResponseEntity<Void> updateUser(@PathVariable Long userId, @RequestBody UserUpdateDTO userUpdateDTO) {
        authService.updateUser(userId, userUpdateDTO);
        return ResponseEntity.ok().build();
    }
}
