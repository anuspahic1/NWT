package com.medicinska.rezervacija.obavijesti_dokumentacija.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "poruka")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Poruka {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PorukaID")
    private Long porukaID;

    @Column(name = "SenderID", nullable = false)
    private Long senderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "SenderType", nullable = false)
    private Notifikacija.Uloga senderType;

    @Column(name = "ReceiverID", nullable = false)
    private Long receiverId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ReceiverType", nullable = false)
    private Notifikacija.Uloga receiverType;

    @Column(name = "Subject", nullable = false)
    private String subject;

    @Column(name = "Content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreationTimestamp
    @Column(name = "Timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;

    @OneToMany(mappedBy = "parentMessage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("timestamp ASC")
    @Builder.Default
    private List<OdgovorNaPoruku> replies = new ArrayList<>();
}