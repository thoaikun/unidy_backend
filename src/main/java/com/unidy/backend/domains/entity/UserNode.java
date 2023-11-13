package com.unidy.backend.domains.entity;

import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("user")

public class UserNode {
    @Id
    @Property("user_id")
    private Integer userId;

    @Property("full_name")
    private String fullName;

    @Property("is_block")
    private Boolean isBlock;
}
