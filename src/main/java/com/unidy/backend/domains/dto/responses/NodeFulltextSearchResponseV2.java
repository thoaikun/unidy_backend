package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import com.unidy.backend.domains.entity.neo4j.PostNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.*;
import software.amazon.awssdk.services.servicecatalog.model.OrganizationNode;

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
        private List<PostNode> postNodesHits;
    }
    private Integer totals;
    private Hits hits;
}
