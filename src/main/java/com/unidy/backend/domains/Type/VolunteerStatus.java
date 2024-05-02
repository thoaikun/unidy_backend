package com.unidy.backend.domains.Type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VolunteerStatus {
    NOT_APPROVE_YET(),
    APPROVE(),
    REJECT();
}