package com.unidy.backend.domains.entity.neo4j;


import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("comment")

public class CommentNode implements Neo4JNode {
    @Id
    @Property("comment_id")
    private Integer comment_id ;

    @Property("comment_json")
    private String commentJson;

    @Relationship(type = "HAS_COMMENT", direction = Relationship.Direction.INCOMING)
    private PostNode postNode;

    @Relationship(type = "REPLY_COMMENT", direction = Relationship.Direction.INCOMING)
    private UserNode userReply;

    @Relationship(type = "WROTE_COMMENT", direction = Relationship.Direction.INCOMING)
    private UserNode userComment;
}
