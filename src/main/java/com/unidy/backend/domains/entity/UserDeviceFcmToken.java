package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_device_fcm_token")
public class UserDeviceFcmToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public Integer id;

    @Column(name = "fcm_token")
    public String fcmToken;

    @Column(name = "user_id")
    public Integer userId;
}
