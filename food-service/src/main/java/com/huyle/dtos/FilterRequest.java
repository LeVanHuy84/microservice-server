package com.huyle.dtos;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequest {
    private Map<String, Object> exact;       // ví dụ: {"status": "ACTIVE"}
    private Map<String, String> regex;       // ví dụ: {"name": "pizza"}
    private Map<String, List<String>> in;    // ví dụ: {"categoryId": ["a", "b"]}
    private Map<String, RangeRequest> range; // ví dụ: {"price": {"from": 10, "to": 50}}
    private int page = 0;
    private int size = 10;
    private String sortBy = "id";
    private String sortDir = "desc";         // asc | desc

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RangeRequest {
        private Double from;
        private Double to;
    }

}
