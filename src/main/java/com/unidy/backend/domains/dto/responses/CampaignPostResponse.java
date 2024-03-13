package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.CampaignNode;
import com.unidy.backend.domains.entity.Organization;
import com.unidy.backend.domains.entity.UserNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Property;
import software.amazon.awssdk.services.servicecatalog.model.OrganizationNode;

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

