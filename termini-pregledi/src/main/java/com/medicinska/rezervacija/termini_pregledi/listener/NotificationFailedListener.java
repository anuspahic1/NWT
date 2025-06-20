package com.medicinska.rezervacija.termini_pregledi.listener;

/*import com.medicinska.rezervacija.termini_pregledi.config.RabbitMQConfig;
import com.medicinska.rezervacija.termini_pregledi.event.NotificationFailedEvent;
import com.medicinska.rezervacija.termini_pregledi.service.PregledService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationFailedListener {

    @Autowired
    private PregledService pregledService;

    @RabbitListener(queues = RabbitMQConfig.APPOINTMENT_NOTIFICATION_FAILED_QUEUE)
    public void handleNotificationFailed(NotificationFailedEvent event) {
        System.out.println("TERMINI-PREGLEDI Listener: Primljen NotificationFailedEvent: " + event);
        // Pozovi kompenzacijsku logiku u PregledService
        // Konvertujemo Long event ID-eve u Integer za PregledService
        pregledService.compensatePregledCreation(event.getAppointmentId().intValue(), event.getTerminId().intValue(), event.getCorrelationId());
    }
}
*/