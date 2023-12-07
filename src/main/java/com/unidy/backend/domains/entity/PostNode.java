package com.unidy.backend.domains.entity;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("post")
public class PostNode {
    @Id
    @Property("post_id")
    private String postId;

    @Property("content")
    private String content;

    @Property("status")
    private String status;

    @Property("create_date")
    private String createDate;

    @Property("update_date")
    private String updateDate;

    @Property("is_block")
    private Boolean isBlock;

    @Property("link_image")
    private String linkImage;

//    @Property("__nodeLabels__")
//    private List<String> nodeLabels;

    @Relationship(type = "HAS_POST", direction = Relationship.Direction.INCOMING)
    private UserNode userNode;
}
