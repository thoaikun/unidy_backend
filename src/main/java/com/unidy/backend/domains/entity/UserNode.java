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
@Node("user")

public class UserNode {
    @Id
    @Property("user_id")
    private Integer userId;

    @Property("user_name")
    private String fullName;

    @Property("is_block")
    private Boolean isBlock;

    @Property("profile_image_link")
    private String profileImageLink;
//    @Property("__nodeLabels__")
//    private List<String> nodeLabels;

//    @Relationship(type = "HAS_POST", direction = Relationship.Direction.OUTGOING)
//    private List<Post> postList;
}
