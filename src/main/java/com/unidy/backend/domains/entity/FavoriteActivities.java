package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "favorite_activities")
public class FavoriteActivities {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer optId;

    @Column(name= "user_id")
    private Integer userId;

    @Column(name = "community_type")
    private Double communityType;

    @Column(name = "education_type")
    private Double education;

    @Column(name = "research_writing_editing")
    private Double research;

    @Column(name = "help_other")
    private Double helpOther;

    @Column(name = "environment")
    private Double environment;

    @Column(name = "healthy")
    private Double healthy;

    @Column(name = "emergency_preparedness")
    private Double emergencyPreparedness;
}
