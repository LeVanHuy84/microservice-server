package com.huyle.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.huyle.models.Food;

public interface FoodRepository extends MongoRepository<Food, String> {
    List<Food> findByRestaurantId(String restaurantId);

    List<Food> findTop10ByOrderByIdDesc();
}
