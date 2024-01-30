package com.unidy.backend.domains.dto.responses;

import com.miragesql.miragesql.annotation.In;
import com.unidy.backend.domains.entity.UserNode;
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
