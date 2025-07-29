package com.huyle.dtos.FoodDtos;

import java.math.BigDecimal;

public record FoodRequest(
    String name,
    String description,
    BigDecimal basePrice,
    String catagoryId,
    String restaurantId
    // Boolean available // optional nếu cần cho phép set trạng thái ngay từ đầu
) {

}
