package com.unidy.backend.domains.entity.neo4j;
import lombok.*;
import org.springframework.data.neo4j.core.schema.*;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("post")
public class PostNode implements Neo4JNode {
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

    @Relationship(type = "HAS_POST", direction = Relationship.Direction.INCOMING)
    private UserNode userNode;

    @Relationship(type = "LIKE", direction = Relationship.Direction.INCOMING)
    private List<UserNode> userLikes ;
}
