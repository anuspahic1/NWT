/*// obavijesti-dokumentacija/src/main/java/com/medicinska/rezervacija/obavijesti_dokumentacija/service/AppointmentNotificationConsumerService.java
package com.medicinska.rezervacija.obavijesti_dokumentacija.service;

import com.medicinska.rezervacija.obavijesti_dokumentacija.config.RabbitMQConfig;
import com.medicinska.rezervacija.obavijesti_dokumentacija.event.AppointmentScheduledEvent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.event.NotificationCreatedEvent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.event.NotificationFailedEvent;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija;
import com.medicinska.rezervacija.obavijesti_dokumentacija.model.Notifikacija.Uloga;
import com.medicinska.rezervacija.obavijesti_dokumentacija.repository.NotifikacijaRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AppointmentNotificationConsumerService {

    @Autowired
    private NotifikacijaRepository notifikacijaRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Slušamo događaje o zakazanom pregledu
    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_SCHEDULED_QUEUE)
    @Transactional // Ovo osigurava da je kreiranje notifikacije unutar transakcije
    public void handleAppointmentScheduled(AppointmentScheduledEvent event) {
        System.out.println("Primljen AppointmentScheduledEvent: " + event);

        try {
            // Kreiranje notifikacije za pacijenta
            Notifikacija patientNotification = Notifikacija.builder()
                    .korisnikID(event.getPatientId()) // Auth userId pacijenta
                    .uloga(Uloga.PACIJENT)
                    .sadrzaj("Vaš pregled kod doktora " + event.getDoctorName() +
                            " je uspješno zakazan za " + event.getAppointmentDateTime().toLocalDate() +
                            " u " + event.getAppointmentDateTime().toLocalTime().toString().substring(0, 5) +
                            ". Razlog: " + event.getAppointmentReason() +
                            ". Id pregleda: " + event.getAppointmentId())
                    .status(Notifikacija.StatusNotifikacije.POSLANO) // Ili PENDNG, ovisno o implementaciji
                    .datumSlanja(LocalDateTime.now())
                    .build();
            Notifikacija savedPatientNotification = notifikacijaRepository.save(patientNotification);
            System.out.println("Kreirana notifikacija za pacijenta: " + savedPatientNotification.getNotifikacijaID());


            // Kreiranje notifikacije za doktora
            Notifikacija doctorNotification = Notifikacija.builder()
                    .korisnikID(event.getDoctorId()) // Auth userId doktora
                    .uloga(Uloga.DOKTOR)
                    .sadrzaj("Imate novi zakazani pregled sa pacijentom " + event.getPatientName() +
                            " za " + event.getAppointmentDateTime().toLocalDate() +
                            " u " + event.getAppointmentDateTime().toLocalTime().toString().substring(0, 5) +
                            ". Id pregleda: " + event.getAppointmentId())
                    .status(Notifikacija.StatusNotifikacije.POSLANO) // Ili PENDNG, ovisno o implementaciji
                    .datumSlanja(LocalDateTime.now())
                    .build();
            Notifikacija savedDoctorNotification = notifikacijaRepository.save(doctorNotification);
            System.out.println("Kreirana notifikacija za doktora: " + savedDoctorNotification.getNotifikacijaID());


            NotificationCreatedEvent successEvent = new NotificationCreatedEvent(
                    savedPatientNotification.getNotifikacijaID(), // ID notifikacije
                    event.getAppointmentId(),
                    event.getCorrelationId(),
                    "Notifikacije uspješno kreirane."
            );
            rabbitTemplate.convertAndSend(RabbitMQConfig.APPOINTMENT_NOTIFICATION_CREATED_QUEUE, successEvent);
            System.out.println("Poslan NotificationCreatedEvent: " + successEvent);

        } catch (Exception e) {
            System.err.println("Greška prilikom obrade AppointmentScheduledEvent i kreiranja notifikacije: " + e.getMessage());
            NotificationFailedEvent failedEvent = new NotificationFailedEvent(
                    event.getAppointmentId(),
                    // Pretpostavka: Ako terminID nije direktno u eventu, moramo ga dohvatiti
                    // Za demo, proslijedićemo 0L ako nije direktno dostupan, ili ga dodaj u AppointmentScheduledEvent
                    // Za sada, koristićemo terminID koji je došao u originalnom DTO-u
                    // Ažurirao sam AppointmentScheduledEvent da ima terminId
                    0L, // Placeholder, trebao bi doći iz eventa, trenutno AppointmentScheduledEvent nema terminID direktno
                    event.getCorrelationId(),
                    e.getMessage()
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.APPOINTMENT_NOTIFICATION_FAILED_QUEUE, failedEvent);
            System.out.println("Poslan NotificationFailedEvent: " + failedEvent);
        }
    }
}*/