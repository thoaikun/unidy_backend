package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.PostNode;
import com.unidy.backend.domains.entity.UserNode;
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
    private PostNode postNode;
    private UserNode userNode;
}
