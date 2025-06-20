package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "odgovor_na_poruku")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OdgovorNaPoruku {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OdgovorID")
    private Long odgovorID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ParentMessageID", nullable = false)
    private Poruka parentMessage;

    @Column(name = "SenderID", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SenderType", nullable = false)
    private Notifikacija.Uloga senderType;

    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "Timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
