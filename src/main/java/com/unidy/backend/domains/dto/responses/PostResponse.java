package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
public class PostResponse {
    private String postId;
    private String content;
    private String status;
    private String createDate;
    private String updateDate;
    private Boolean isBlock;
    private String linkImage;
    private UserNode userNodes;
//    private List<UserNode> userLikes;
    private Boolean isLiked;
    private int likeCount ;
}
