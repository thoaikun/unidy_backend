package com.unidy.backend.domains.dto.responses;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Builder
@Getter
@Setter
public class PostResponse {
    private Integer optId;
    private String content;
    private Date createDate;
    private Date updateDate;
    private Boolean isBlock;
    private String linkImage;
}
