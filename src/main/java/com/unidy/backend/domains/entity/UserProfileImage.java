package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile_image")
public class UserProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "image_id")
    private int imageId;

    @Column(name = "link_image")
    private String linkImage;

    @Column(name = "update_date")
    private Date updateDate;

    @Column(name = "user_id")
    private int userId;
}
