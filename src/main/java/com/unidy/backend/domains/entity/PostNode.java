package com.unidy.backend.domains.entity;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Date;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("Post")
public class PostNode {
    @Id
    @Property("post_id")
    private Integer postId;

    @Property("content")
    private String content;

    @Property("create_date")
    private Date createDate;

    @Property("update_date")
    private Date updateDate;

    @Property("is_block")
    private Boolean isBlock;

    @Property("link_image")
    private String linkImage;
}
