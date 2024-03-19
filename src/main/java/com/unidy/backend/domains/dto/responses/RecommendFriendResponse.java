package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class RecommendFriendResponse {
    private UserNode fiendSuggest;
    private int numOfMutualFriend;
    private List<UserNode> mutualFriends;
}
