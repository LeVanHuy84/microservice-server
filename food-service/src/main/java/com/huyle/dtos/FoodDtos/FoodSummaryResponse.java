package com.huyle.dtos.FoodDtos;

import java.math.BigDecimal;

public record FoodSummaryResponse(
    String id,
    String name,
    BigDecimal basePrice,
    String imageUrl
) {

}