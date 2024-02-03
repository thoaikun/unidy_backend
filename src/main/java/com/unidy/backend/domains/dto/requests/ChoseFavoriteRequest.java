package com.unidy.backend.domains.dto.requests;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChoseFavoriteRequest {
    private Double community_type;
    private Double education_type;
    private Double research_writing_editing;
    private Double help_other;
    private Double environment;
    private Double healthy;
    private Double emergency_preparedness;
}
