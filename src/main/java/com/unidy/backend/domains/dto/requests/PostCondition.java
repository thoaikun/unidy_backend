package com.unidy.backend.domains.dto.requests;

import com.unidy.backend.domains.Type.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCondition {
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date fromDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date toDate;
    private CampaignStatus status;
    private int limit;
    private int skip;
}
