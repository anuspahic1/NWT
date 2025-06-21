package com.system_events.entity; // Paket za vaš entitet

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column; // Dodano za specifično mapiranje kolona

@Entity // Označava da je ovo JPA entitet
@Table(name = "system_events") // Naziv tabele u bazi podataka
public class EventEntity {

    @Id // Označava primarni ključ
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatsko generisanje ID-a
    private Long id;

    @Column(name = "timestamp_seconds") // Mapira na kolonu u bazi, ako se razlikuje od naziva polja
    private Long timestamp; // Vremenski pečat akcije (Unix timestamp - sekunde)

    @Column(name = "microservice_name")
    private String microserviceName; // Naziv mikroservisa

    @Column(name = "user_id")
    private String userId; // ID korisnika

    @Column(name = "action_type")
    private String actionType; // Tip akcije (CREATE, READ, UPDATE, DELETE) - String reprezentacija enum-a

    @Column(name = "resource_name")
    private String resourceName; // Naziv resursa

    @Column(name = "resource_identifier")
    private String resourceIdentifier; // ID ili jedinstveni identifikator resursa

    @Column(name = "response_status") // Mapirano sa ResponseStatus enum-a iz .proto
    private String responseStatus; // Status odgovora (SUCCESS, ERROR) - String reprezentacija enum-a

    @Column(name = "error_message") // Mapirano sa error_message iz .proto
    private String errorMessage; // Poruka o grešci

    // Opciono: Polje za additional_info mapu (može biti JSON string ili drugi format)
    // @Column(name = "additional_info", columnDefinition = "TEXT") // Primjer za TEXT kolonu
    // private String additionalInfoJson;

    // Prazan konstruktor je potreban za JPA
    public EventEntity() {
    }

    // Getteri i Setteri (Generisani ili ručno napisani)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMicroserviceName() {
        return microserviceName;
    }

    public void setMicroserviceName(String microserviceName) {
        this.microserviceName = microserviceName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Getter i Setter za additionalInfoJson (ako ste ga dodali)
    // public String getAdditionalInfoJson() {
    //     return additionalInfoJson;
    // }
    //
    // public void setAdditionalInfoJson(String additionalInfoJson) {
    //     this.additionalInfoJson = additionalInfoJson;
    // }

    // Možete dodati i toString() metodu za lakše logiranje/debugiranje
    @Override
    public String toString() {
        return "EventEntity{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", microserviceName='" + microserviceName + '\'' +
                ", userId='" + userId + '\'' +
                ", actionType='" + actionType + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", resourceIdentifier='" + resourceIdentifier + '\'' +
                ", responseStatus='" + responseStatus + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                // ", additionalInfoJson='" + additionalInfoJson + '\'' + // Ako ste ga dodali
                '}';
    }
}
