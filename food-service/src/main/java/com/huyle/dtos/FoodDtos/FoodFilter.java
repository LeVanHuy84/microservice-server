package com.huyle.dtos.FoodDtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FoodFilter {
    private Boolean available;
    private String name;
    private String categoryId;
    private Double minPrice;
    private Double maxPrice;
    private int page = 0;
    private int size = 10;
}
