package com.unidy.backend.domains.entity.neo4j;

import com.unidy.backend.domains.entity.relationship.DonateRelationship;
import lombok.*;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Node("campaign")
public class CampaignNode implements Neo4JNode {
    @Id
    @Property("campaign_id")
    private String campaignId;

    @Property("title")
    private String title;

    @Property("hash_tag")
    private String hashTag;

    @Property("content")
    private String content;

    @Property("status")
    private String status;

    @Property("start_date")
    private String startDate;

    @Property("end_date")
    private String endDate;

    @Property("time_take_place")
    private String timeTakePlace;

    @Property("location")
    private String location;

    @Property("num_of_register")
    private int numOfRegister;

    @Property("create_date")
    private String createDate;

    @Property("update_date")
    private String updateDate;

    @Property("is_block")
    private Boolean isBlock;

    @Property("link_image")
    private String linkImage;

    @Property("donation_budget")
    private int donationBudget;

    @Property("donation_budget_received")
    private int donationBudgetReceived;

    @Relationship(type = "HAS_CAMPAIGN", direction = Relationship.Direction.INCOMING)
    private UserNode userNode;

//    @Relationship(type = "REGISTER", direction = Relationship.Direction.INCOMING)
//    private UserNode userRegister;

    @Relationship(type = "DONATE", direction = Relationship.Direction.INCOMING)
    private DonateRelationship donate;

    @Relationship(type = "LIKE", direction = Relationship.Direction.INCOMING)
    private List<UserNode> userLikes ;
}
