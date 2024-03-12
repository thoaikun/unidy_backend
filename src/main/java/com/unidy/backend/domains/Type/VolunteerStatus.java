package com.unidy.backend.domains.Type;

import com.unidy.backend.domains.role.Permission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum VolunteerStatus {
    NOT_APPROVE_YET(),
    APPROVED(),
    BLOCK();
}