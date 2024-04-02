package com.unidy.backend.domains.Type;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CampaignStatus {
    IN_PROGRESS(),
    COMPLETE(),
    BLOCK();
}
