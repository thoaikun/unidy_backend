package com.unidy.backend.domains.dto.notification;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
public class ExtraData {
    private String key;
    private String value;
}