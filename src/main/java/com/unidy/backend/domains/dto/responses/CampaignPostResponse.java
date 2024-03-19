package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Builder
@Getter
@Setter
public class CampaignPostResponse {
    @Data
    @Builder
    public static class CampaignPostResponseData {
        private CampaignNode campaign;
        private UserNode organizationNode;
        private Boolean isLiked;
        private int likeCount ;
    }

    private List<CampaignPostResponseData> campaigns;
    private String nextCursor;
    private int nextOffset;
}

