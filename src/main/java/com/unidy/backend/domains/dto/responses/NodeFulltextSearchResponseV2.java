package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class NodeFulltextSearchResponseV2 {
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Hits {
        private List<UserNode> volunteerNodesHits;
        private List<UserNode> organizationNodesHits;
        private List<CampaignPostResponse.CampaignPostResponseData> campaignPostResponseDataHits;
        private List<PostResponse> postNodesHits;
    }
    private Integer totals;
    private Hits hits;
}
