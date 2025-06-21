package com.system_events.service; // Vaš paket za servis klase

// Ažurirani importi na osnovu vašeg .proto fajla i generisanog paketa com.yourcompany.events.grpc
import grpc.*;
import com.google.protobuf.Timestamp; // Import Google Timestamp klase

import com.system_events.repository.EventRepository; // Pretpostavljeni paket za repozitorijum
import com.system_events.entity.EventEntity; // Pretpostavljeni paket za entitet

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant; // Za konverziju Timestamp-a
import java.util.Map; // Za additional_info mapu

@GrpcService // Spring anotacija za gRPC servis
// Nasljeđivanje klase na osnovu generisanog naziva servisa iz vašeg .proto
public class SystemEventsGrpcService extends EventLoggingServiceGrpc.EventLoggingServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(SystemEventsGrpcService.class);

    // Injektovanje repozitorijuma za rad sa bazom podataka
    @Autowired
    private EventRepository eventRepository;

    @Override
    // Ažuriran naziv metode i tipovi parametara na osnovu vašeg .proto
    public void logEvent(SystemEvent request, StreamObserver<LogEventResponse> responseObserver) {
        log.info("Received SystemEvent: Timestamp: {}, Microservice: {}, Action: {}, Resource: {}, Status: {}",
                request.getTimestamp(),
                request.getMicroserviceName(),
                request.getActionType(),
                request.getResourceName(),
                request.getResponseStatus());

        // Inicijalizacija response varijable na null ili default vrijednost
        LogEventResponse response = null;

        try {
            // Kreiranje entiteta za bazu podataka iz primljene gRPC poruke
            EventEntity eventEntity = new EventEntity();

            // Konverzija Google Timestamp u Java Instant (ili Long Unix timestamp)
            Timestamp grpcTimestamp = request.getTimestamp();
            if (grpcTimestamp != null) {
                eventEntity.setTimestamp(grpcTimestamp.getSeconds()); // Čuvamo kao Unix timestamp (sekunde)
            } else {
                eventEntity.setTimestamp(null); // Postavite na null ili default vrijednost
            }

            eventEntity.setMicroserviceName(request.getMicroserviceName());

            // Provjera i postavljanje userId (nije optional u .proto, provjeravamo prazan string)
            String userId = request.getUserId();
            if (userId != null && !userId.isEmpty()) {
                eventEntity.setUserId(userId);
            } else {
                eventEntity.setUserId(null); // Postavite na null ili default vrijednost ako nije poslan
            }

            // Konverzija ActionType enum-a u String
            eventEntity.setActionType(request.getActionType().toString());

            eventEntity.setResourceName(request.getResourceName());

            // Provjera i postavljanje resourceIdentifier (nije optional u .proto, provjeravamo prazan string)
            String resourceIdentifier = request.getResourceIdentifier();
            if (resourceIdentifier != null && !resourceIdentifier.isEmpty()) {
                eventEntity.setResourceIdentifier(resourceIdentifier); // Pretpostavka da EventEntity ima ovo polje
            } else {
                eventEntity.setResourceIdentifier(null);
            }

            // Konverzija ResponseStatus enum-a u String i postavljanje u EventEntity
            // Ispravljen naziv metode na setResponseStatus
            eventEntity.setResponseStatus(request.getResponseStatus().toString()); // Koristimo ResponseStatus u EventEntity

            // Provjera i postavljanje errorMessage (nije optional u .proto, provjeravamo prazan string)
            String errorMessage = request.getErrorMessage();
            if (errorMessage != null && !errorMessage.isEmpty()) {
                eventEntity.setErrorMessage(errorMessage); // Mapiramo errorMessage na errorMessage u EventEntity (ili details ako ste tako nazvali)
            } else {
                eventEntity.setErrorMessage(null); // Postavite na null ili default vrijednost
            }

            // Opciono: Rukovanje additional_info mapom ako je potrebno sačuvati je
            // Map<String, String> additionalInfo = request.getAdditionalInfoMap();
            // Ako EventEntity ima polje za mapu ili JSON string, sačuvajte je.


            // Snimanje entiteta u bazu podataka
            eventRepository.save(eventEntity);

            log.info("SystemEvent successfully saved to database.");

            // Priprema uspješnog odgovora (LogEventResponse)
            response = LogEventResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("SystemEvent logged successfully")
                    .build();

        } catch (Exception e) {
            // Rukovanje greškom pri snimanju u bazu
            log.error("Error saving SystemEvent to database", e);

            // Priprema odgovora sa greškom (LogEventResponse)
            response = LogEventResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Error logging SystemEvent: " + e.getMessage())
                    .build();
        } finally {
            // Slanje odgovora klijentu samo ako je inicijalizovan
            if (response != null) {
                responseObserver.onNext(response);
            } else {
                // Ako response nije inicijalizovan zbog greške prije try/catch blokova
                // Pošaljite generički odgovor o grešci
                LogEventResponse errorResponse = LogEventResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("An unexpected error occurred before processing the event.")
                        .build();
                responseObserver.onNext(errorResponse);
            }
            responseObserver.onCompleted();
        }
    }
}
