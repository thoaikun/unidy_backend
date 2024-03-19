package com.unidy.backend.domains.dto.responses;

import com.unidy.backend.domains.entity.neo4j.Neo4JNode;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@Builder
public class NodeFulltextSearchResponse {
    private Integer totals;
    private List<Neo4JNode> hits;
}
