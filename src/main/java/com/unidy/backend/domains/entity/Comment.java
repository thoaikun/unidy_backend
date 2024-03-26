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
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "content")
    private String content;

    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "is_block")
    private Boolean idBlock;

    @Column(name = "reply_by_comment")
    private Integer replyByComment;
}
