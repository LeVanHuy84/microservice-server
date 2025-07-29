package com.huyle.repositories;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.huyle.common.mongo.BaseRepositoryCustom;
import com.huyle.dtos.FilterRequest;
import com.huyle.dtos.FoodDtos.FoodFilter;
import com.huyle.models.Food;

@Repository
public class FoodRepositoryCustomImpl extends BaseRepositoryCustom<Food> {

    public Page<Food> searchFoods(FoodFilter filter) {
        FilterRequest filterRequest = toFilterRequest(filter);
        return filterWithDynamicQuery(Food.class, filterRequest);
    }

    private FilterRequest toFilterRequest(FoodFilter filter) {
        FilterRequest request = new FilterRequest();
        request.setExact(Map.of(
            "available", filter.isAvailable(),
            "catagoriId", filter.getCategoryId()
        ));
        request.setRange(Map.of(
            "price", new FilterRequest.RangeRequest(filter.getMinPrice(), filter.getMaxPrice())
        ));
        request.setPage(filter.getPage());
        request.setSize(filter.getSize());
        return request;
    }
}
