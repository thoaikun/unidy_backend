package com.unidy.backend.domains.entity.relationship;

import com.unidy.backend.domains.entity.CampaignNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RelationshipProperties
//@RelationshipEntity(type="DONATE")

public class DonateRelationship {
    @RelationshipId
    @GeneratedValue
    private Long id;

    @Property(name="donate")
    private int donate;

    @TargetNode
    private CampaignNode campaignNode;
}
