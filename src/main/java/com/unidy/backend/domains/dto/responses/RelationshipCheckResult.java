package com.unidy.backend.domains.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelationshipCheckResult {
    private boolean isFriend;
    private boolean isRequested;
    private boolean isRequesting;
    private boolean isFollowed;
}
