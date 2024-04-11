package com.unidy.backend.domains.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "post")
public class Post {
    @Id
    @Column(name = "post_id")
    private String postId;

    @Column(name = "content")
    private String content;

    @Column(name = "status")
    private String status;

    @Column(name = "create_date")
    @Builder.Default
    private Timestamp createDate = new Timestamp(System.currentTimeMillis());

    @Column(name = "update_date")
    @Builder.Default
    private Timestamp updateDate = new Timestamp(System.currentTimeMillis());;

    @Column(name = "is_block")
    @Builder.Default
    private Boolean isBlock = false;

    @Column(name = "link_image")
    private String linkImage;

    @Column(name = "user_id")
    private Integer userId;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;
}
