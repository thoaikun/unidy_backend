package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "campaign")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "campaign_id")
    private Integer campaignId;

    @Column(name= "content")
    private String content;

    @Column(name = "numbers_volunteer")
    private Integer numberVolunteer;

    @Column(name = "start_day")
    private Date startDate;

    @Column(name = "end_day")
    private Date endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "status")
    private String status;

    @Column(name = "create_day")
    private Date createDate;

    @Column(name = "update_day")
    private Date updateDate;

    @Column(name = "update_by")
    private Integer updateBy;
}
