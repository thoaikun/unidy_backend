package com.unidy.backend.domains.entity.neo4j;

import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("user")
public class UserNode implements Neo4JNode {
    @Id
    @Property("user_id")
    private Integer userId;

    @Property("user_name")
    private String fullName;

    @Property("is_block")
    private Boolean isBlock;

    @Property("profile_image_link")
    private String profileImageLink;

    @Property("role")
    private String role;

    @Nullable
    private Boolean isFriend;

    @Nullable
    private Boolean isFollow;
}
