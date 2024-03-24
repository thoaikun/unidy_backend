package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.CommentNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentReplyResponse {
    private UserNode user;
    private CommentNode comment;
    private Boolean haveReply;
}
