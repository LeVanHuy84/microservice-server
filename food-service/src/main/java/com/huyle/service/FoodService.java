package com.huyle.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.huyle.dtos.PagedResponse;
import com.huyle.dtos.FoodDtos.FoodDetailResponse;
import com.huyle.dtos.FoodDtos.FoodFilter;
import com.huyle.dtos.FoodDtos.FoodRequest;
import com.huyle.dtos.FoodDtos.FoodSummaryResponse;
import com.huyle.dtos.FoodDtos.RestaurantFoodResponse;
import com.huyle.mappers.FoodMapper;
import com.huyle.models.Food;
import com.huyle.repositories.CatagoryRepository;
import com.huyle.repositories.FoodRepository;
import com.huyle.repositories.FoodRepositoryCustomImpl;
import com.huyle.utils.ImageInfo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodRepositoryCustomImpl foodRepositoryCustom;
    private final CatagoryRepository catagoryRepository;
    private final FoodMapper foodMapper;
    private final CloudinaryService cloudinaryService;

    public List<FoodSummaryResponse> getTop10Foods() {
        List<Food> topFoods = foodRepository.findTop10ByOrderByIdDesc();
        return foodMapper.toSumaryResponses(topFoods);
    }

    public PagedResponse<FoodSummaryResponse> searchFoods(FoodFilter request) {
        Page<Food> page = foodRepositoryCustom.searchFoods(request);

        PagedResponse<FoodSummaryResponse> response = new PagedResponse<>(
            page.getContent().stream()
                .map(foodMapper::toSumaryResponse)
                .toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
        return response;
    }

    public FoodDetailResponse getFoodDetailById(String id) {
        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + id));
        return foodMapper.toFoodDetailResponse(food);
    }

    public RestaurantFoodResponse getRestaurantFoodDetailById(String id) {
        Food food = foodRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + id));
        return foodMapper.toRestaurantFoodResponse(food);
    }

    public void createFood(FoodRequest food) {
        try {
            catagoryRepository.findById(food.catagoryId())
                .orElseThrow(() -> new IllegalArgumentException("Catagory not found with id: " + food.catagoryId()));
            Food foodEntity = foodMapper.toEntity(food);
            foodRepository.save(foodEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void updateFoodInfo(String id, FoodRequest food) {
        try {
            Food foodEntity = foodRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + id));
            foodMapper.updateEntityFromDto(food, foodEntity);
            foodRepository.save(foodEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void uploadFoodImages(List<MultipartFile> files, String foodId) {
        List<ImageInfo> imageInfos;
        try {
            if(files == null || files.isEmpty()) {
                throw new IllegalArgumentException("No files provided for upload");
            }
            Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + foodId));
            imageInfos = cloudinaryService.uploadMultipleImages(files);

            List<ImageInfo> currentImages = food.getImages();
            if (currentImages == null) {
                currentImages = new ArrayList<>();
            }
            currentImages.addAll(imageInfos);
            food.setImages(currentImages); // Assuming only one image is uploaded
            
            foodRepository.save(food);
        } catch (IOException | IllegalArgumentException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteFoodImages(List<String> publicIds, String foodId) {
        try {
            if(publicIds == null || publicIds.isEmpty()) {
                throw new IllegalArgumentException("No public IDs provided for deletion");
            }
            Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + foodId));
            cloudinaryService.deleteMultipleImages(publicIds);
            food.getImages().removeIf(image -> publicIds.contains(image.getPublicId()));
            foodRepository.save(food);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteFood(String id) {
        foodRepository.deleteById(id);
    }
}
