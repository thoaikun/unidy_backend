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
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "achievement_id")
    private Integer achievementId;

    @Column(name= "volunteer_id")
    private Integer volunteer_id;

    @Column(name= "content")
    private String content;
}
