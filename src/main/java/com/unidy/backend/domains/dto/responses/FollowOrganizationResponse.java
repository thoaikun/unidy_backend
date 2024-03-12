package com.unidy.backend.domains.dto.responses;

import jakarta.annotation.Nullable;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class FollowOrganizationResponse {
    private String message;
    @Nullable
    private String topic;
}
