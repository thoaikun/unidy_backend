package com.unidy.backend.domains.entity;

import com.google.api.client.util.DateTime;
import com.unidy.backend.domains.Type.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "receiver")
    private int receiverId;

    @Column(name = "owner")
    private int ownerId;

    @Column(name = "type")
    private String type;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "extra")
    private String extra;

    @Column(columnDefinition = "TIMESTAMP DEFAULT NOW()")
    @Builder.Default
    Timestamp createdTime = new Timestamp(System.currentTimeMillis());

    @Column(name = "seen_time")
    Timestamp seenTime;
}
