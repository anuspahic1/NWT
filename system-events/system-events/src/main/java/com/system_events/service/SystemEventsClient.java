package com.system_events.service; // Vaš paket za servis klase

// Ažurirani importi na osnovu vašeg .proto fajla i generisanog paketa com.yourcompany.events.grpc
import grpc.*;
import com.google.protobuf.Timestamp; // Import Google Timestamp klase

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant; // Za kreiranje Timestamp-a
import java.util.Map; // Za additional_info mapu

@Service
public class SystemEventsClient {

    private static final Logger log = LoggerFactory.getLogger(SystemEventsClient.class);

    // Injektovanje gRPC klijent stub-a za EventLoggingService.
    // Tip mora odgovarati generisanoj klasi na osnovu naziva servisa u .proto fajlu (EventLoggingServiceGrpc).
    @GrpcClient("system-events-service") // Naziv gRPC kanala definisan u konfiguraciji (application.properties)
    private EventLoggingServiceGrpc.EventLoggingServiceBlockingStub blockingStub; // Koristimo BlockingStub za sinhroni poziv

    /**
     * Šalje sistemski događaj System Events servisu putem gRPC-a.
     * @param microserviceName Naziv mikroservisa iz kojeg se šalje događaj.
     * @param userId ID korisnika koji je pokrenuo akciju (može biti null).
     * @param actionType Tip akcije (CREATE, GET, UPDATE, DELETE) kao enum.
     * @param resourceName Naziv resursa nad kojim se dešava akcija.
     * @param resourceIdentifier ID ili jedinstveni identifikator resursa (može biti null).
     * @param responseStatus Status odgovora (SUCCESS, ERROR) kao enum.
     * @param errorMessage Poruka o grešci ako je status ERROR (može biti null).
     * @param additionalInfo Mapa dodatnih informacija (opciono, može biti null).
     */
    public void logSystemEvent(
            String microserviceName,
            String userId,
            ActionType actionType, // Koristimo ActionType enum iz generisanog koda
            String resourceName,
            String resourceIdentifier, // Dodano polje resource_identifier
            ResponseStatus responseStatus, // Koristimo ResponseStatus enum iz generisanog koda
            String errorMessage,
            Map<String, String> additionalInfo // Dodana mapa za additional_info
    ) {

        try {
            // Kreiranje Google Timestamp-a od trenutnog vremena
            Instant now = Instant.now();
            Timestamp timestamp = Timestamp.newBuilder()
                    .setSeconds(now.getEpochSecond())
                    .setNanos(now.getNano())
                    .build();

            // Kreiranje SystemEvent poruke na osnovu vašeg .proto fajla
            SystemEvent.Builder eventBuilder = SystemEvent.newBuilder()
                    .setTimestamp(timestamp)
                    .setMicroserviceName(microserviceName)
                    .setActionType(actionType) // Postavljamo ActionType enum
                    .setResourceName(resourceName)
                    .setResponseStatus(responseStatus); // Postavljamo ResponseStatus enum

            // Postavljanje opcionalnih polja samo ako nisu null
            if (userId != null) {
                eventBuilder.setUserId(userId);
            }
            if (resourceIdentifier != null) {
                eventBuilder.setResourceIdentifier(resourceIdentifier);
            }
            if (errorMessage != null) {
                eventBuilder.setErrorMessage(errorMessage);
            }
            // Ako šaljete additional_info mapu:
            if (additionalInfo != null && !additionalInfo.isEmpty()) {
                eventBuilder.putAllAdditionalInfo(additionalInfo);
            }


            SystemEvent systemEvent = eventBuilder.build();

            // Korištenje blocking stub-a za sinhroni poziv metode logEvent
            LogEventResponse response = blockingStub.logEvent(systemEvent); // Pozivamo logEvent metodu

            log.info("Log Event Response: Success: {}, Message: {}", response.getSuccess(), response.getMessage());

        } catch (Exception e) {
            log.error("Error logging system event via gRPC", e);
            // Ovdje implementirajte logiku za rukovanje greškama (npr. ponovni pokušaj, slanje u queue)
        }
    }
}
