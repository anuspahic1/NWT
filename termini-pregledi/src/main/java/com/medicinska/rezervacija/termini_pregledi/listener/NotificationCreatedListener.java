/*package com.medicinska.rezervacija.termini_pregledi.listener;

import com.medicinska.rezervacija.termini_pregledi.config.RabbitMQConfig;
import com.medicinska.rezervacija.termini_pregledi.event.NotificationCreatedEvent;
import com.medicinska.rezervacija.termini_pregledi.service.PregledService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationCreatedListener {

    @Autowired
    private PregledService pregledService;

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_NOTIFICATION_CREATED_QUEUE)
    public void handleNotificationCreated(NotificationCreatedEvent event) {
        System.out.println("TERMINI-PREGLEDI Listener: Primljen NotificationCreatedEvent: " + event);
        // Ovdje pozivamo metodu u servisu koja će finalizirati Sagu.
        // Konvertujemo Long event ID u Integer za PregledService
        pregledService.finalizeAppointmentCreation(event.getAppointmentId().intValue(), event.getCorrelationId());
    }
}
*/