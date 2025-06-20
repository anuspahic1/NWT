package com.medicinska.rezervacija.obavijesti_dokumentacija.controller;

import com.medicinska.rezervacija.obavijesti_dokumentacija.dto.NotifikacijaDTO;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/obavijesti")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/notifikacije/send")
    public NotifikacijaDTO sendNotification(@RequestParam Long korisnikID,
                                            @RequestParam String message,
                                            @RequestParam Notifikacija.Uloga uloga) {
        return notificationService.sendNotification(korisnikID, message, uloga);
    }

    @GetMapping("/notifikacije/{id}")
    public NotifikacijaDTO getNotificationById(@PathVariable Long id) {
        return notificationService.getNotificationById(id);
    }


    @GetMapping("/notifikacije/doktor/{doktorId}")
    public List<NotifikacijaDTO> getDoctorNotifications(@PathVariable Long doktorId) {
        return notificationService.getNotificationsForUser(doktorId, Notifikacija.Uloga.DOKTOR);
    }

    @PutMapping("/notifikacije/{id}/procitana")
    public ResponseEntity<NotifikacijaDTO> markNotificationAsRead(@PathVariable Long id) {
        NotifikacijaDTO updatedNotif = notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok(updatedNotif);
    }
}
