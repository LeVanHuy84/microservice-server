package com.huyle.mappers;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.huyle.dtos.FoodDtos.FoodDetailResponse;
import com.huyle.dtos.FoodDtos.FoodRequest;
import com.huyle.dtos.FoodDtos.FoodSummaryResponse;
import com.huyle.dtos.FoodDtos.RestaurantFoodResponse;
import com.huyle.models.Food;
import com.huyle.utils.ImageInfo;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodMapper {

    Food toEntity(FoodRequest dto);

    @Mapping(target = "imageUrls", expression = "java(mapImageUrls(entity.getImages()))")
    FoodDetailResponse toFoodDetailResponse(Food entity);

    @Mapping(target = "imageUrl", expression = "java(getFirstImageUrl(entity.getImages()))")
    FoodSummaryResponse toSumaryResponse(Food entity);

    List<FoodSummaryResponse> toSumaryResponses(List<Food> entities);

    RestaurantFoodResponse toRestaurantFoodResponse(Food entity);

    @BeanMapping(ignoreByDefault = false)
    void updateEntityFromDto(FoodRequest dto, @MappingTarget Food entity);

    // === default methods for mapping ===
    default List<String> mapImageUrls(List<ImageInfo> images) {
        if (images == null) return List.of();
        return images.stream()
                     .map(ImageInfo::getUrl)
                     .toList();
    }

    default String getFirstImageUrl(List<ImageInfo> images) {
        if (images == null || images.isEmpty()) return null;
        return images.get(0).getUrl();
    }
}
