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
@Data
@Builder
@Getter
@Setter
public class CampaignPostResponse {
//    private String campaignId;
//    private String hashTag;
//    private String content;
//    private String status;
//    private String startDate;
//    private String endDate;
//    private String timeTakePlace;
//    private String location;
//    private int numOfRegister;
//    private String createDate;
//    private String updateDate;
//    private Boolean isBlock;
//    private String linkImage;
    private CampaignNode campaign;
    private UserNode organizationNode;
    private Boolean isLiked;
    private int likeCount ;
}
