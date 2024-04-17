package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class PostResponse implements Neo4JNode {
    private String postId;
    private String content;
    private String status;
    private String createDate;
    private String updateDate;
    private Boolean isBlock;
    private String linkImage;
    private UserNode userNode;
//    private List<UserNode> userLikes;
    private Boolean isLiked;
    private int likeCount ;
    private int numberComments;
}
