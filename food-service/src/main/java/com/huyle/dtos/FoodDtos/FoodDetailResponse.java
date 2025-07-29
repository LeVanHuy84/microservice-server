package com.huyle.dtos.FoodDtos;

import java.math.BigDecimal;
import java.util.List;

public record FoodDetailResponse(
    String id,
    String name,
    String description,
    BigDecimal basePrice,
    List<String> imageUrl,
    String category,
    String restaurantId,
    boolean available
) {

}
