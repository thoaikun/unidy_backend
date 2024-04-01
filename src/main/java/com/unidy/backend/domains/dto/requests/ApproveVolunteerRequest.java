package com.unidy.backend.domains.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveVolunteerRequest {
    private List<Integer> volunteerIds;
}
