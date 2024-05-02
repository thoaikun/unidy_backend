package com.unidy.backend.domains.dto.responses;
import com.unidy.backend.domains.Type.VolunteerStatus;
import com.unidy.backend.domains.entity.neo4j.CampaignNode;
import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import com.unidy.backend.domains.entity.neo4j.UserNode;
import jakarta.annotation.Nullable;
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
    public static class CampaignPostResponseData implements Neo4JNode {
        private CampaignNode campaign;
        private UserNode organizationNode;
        private int likeCount;
        private Boolean isLiked;
        @Builder.Default
        private Boolean isJoined = false;
        @Builder.Default
        private VolunteerStatus joinedStatus = null;
        private int numberComments;
    }

    private List<CampaignPostResponseData> campaigns;
    private Integer total;
    @Nullable
    private Integer nextOffset;
}

