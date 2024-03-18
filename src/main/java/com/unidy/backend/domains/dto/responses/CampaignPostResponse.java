package com.unidy.backend.domains.dto.responses;

import com.miragesql.miragesql.annotation.In;
import com.unidy.backend.domains.entity.CampaignNode;
import com.unidy.backend.domains.entity.Organization;
import com.unidy.backend.domains.entity.UserNode;
import jakarta.annotation.Nullable;
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
        private int likeCount ;
        private Boolean isLiked;
        @Builder.Default
        private Boolean isJoined = false;
    }

    private List<CampaignPostResponseData> campaigns;
    @Nullable
    private String nextCursor;
    @Nullable
    private Integer nextOffset;
}

