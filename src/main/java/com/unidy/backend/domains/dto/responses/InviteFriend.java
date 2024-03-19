package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteFriend {
    private UserNode userRequest;
    private String requestAt;
}
