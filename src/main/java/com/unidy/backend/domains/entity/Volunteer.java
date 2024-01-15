package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "volunteer")
public class Volunteer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private int volunteerId;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "sponsor_íd")
    private int sponsorId;

    @Column(name = "organization_id")
    private String organizationId;
}
