package com.unidy.backend.domains.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolunteerDonationResponse {
    private int userId;
    private String fullName;
    private String achievement;
    private int transactionId;
    private Long donationBudget;
}
