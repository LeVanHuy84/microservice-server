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
    String categoryId,
    String restaurantId,
    boolean available,
    double totalRating,
    int totalFeedback,
    int totalOrder
) {

}
