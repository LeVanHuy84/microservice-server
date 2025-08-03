package com.huyle.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.huyle.dtos.PagedResponse;
import com.huyle.dtos.FoodDtos.FoodDetailResponse;
import com.huyle.dtos.FoodDtos.FoodFilter;
import com.huyle.dtos.FoodDtos.FoodRequest;
import com.huyle.dtos.FoodDtos.FoodSummaryResponse;
import com.huyle.dtos.FoodDtos.RestaurantFoodResponse;
import com.huyle.service.FoodService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping("/top-10")
    public ResponseEntity<List<FoodSummaryResponse>> getTop10Foods() {
        List<FoodSummaryResponse> topFoods = foodService.getTop10Foods();
        return ResponseEntity.ok(topFoods);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedResponse<FoodSummaryResponse>> filterFoods(@ModelAttribute FoodFilter request) {
        PagedResponse<FoodSummaryResponse> result = foodService.searchFoods(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<FoodDetailResponse> getFoodDetail(@PathVariable String id) {
        return ResponseEntity.ok(foodService.getFoodDetailById(id));
    }

    @GetMapping("/restaurant-food/{id}")
    public ResponseEntity<RestaurantFoodResponse> getRestaurantFood(@PathVariable String id) {
        return ResponseEntity.ok(foodService.getRestaurantFoodDetailById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createFood(@RequestBody FoodRequest food) {
        foodService.createFood(food);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateFood(@PathVariable String id, @RequestBody FoodRequest food) {
        foodService.updateFoodInfo(id, food);
    }

    @PostMapping(path = "/{foodId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void uploadFoodImages(
            @RequestParam("files") List<MultipartFile> files,
            @PathVariable String foodId
    ) {
        foodService.uploadFoodImages(files, foodId);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable String id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }
}
