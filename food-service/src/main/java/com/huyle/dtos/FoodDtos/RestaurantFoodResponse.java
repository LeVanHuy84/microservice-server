package com.huyle.dtos.FoodDtos;

import java.math.BigDecimal;
import java.util.List;

import com.huyle.utils.ImageInfo;

public record RestaurantFoodResponse(
    String id,
    String name,
    String description,
    BigDecimal basePrice,
    List<ImageInfo> images,
    String category,
    String restaurantId,
    boolean available
) {

}
