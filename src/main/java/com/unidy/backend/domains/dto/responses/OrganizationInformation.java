package com.unidy.backend.domains.dto.responses;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationInformation {
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverallFigure {
        private int totalCampaign;
        private int totalVolunteer;
        private int totalTransaction;
        private int totalTransactionInDay;
    }

    private int userId;
    private String organizationName;
    private String address;
    private String phone;
    private String email;
    private String country;
    private String image;
    private String firebaseTopic;
    private Boolean isFollow;
    private OverallFigure overallFigure;

    public OrganizationInformation(
        Integer userId,
        String organizationName,
        String address,
        String phone,
        String email,
        String country,
        String image,
        String firebaseTopic,
        Boolean isFollow
    ) {
        this.userId = userId;
        this.organizationName = organizationName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.country = country;
        this.image = image;
        this.firebaseTopic = firebaseTopic;
        this.isFollow = isFollow;
    }
}
