package com.huyle.repositories;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import com.huyle.common.mongo.BaseRepositoryCustom;
import com.huyle.dtos.FilterRequest;
import com.huyle.dtos.FoodDtos.FoodFilter;
import com.huyle.models.Food;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FoodRepositoryCustomImpl extends BaseRepositoryCustom<Food> {

    public Page<Food> searchFoods(FoodFilter filter) {
        FilterRequest filterRequest = toFilterRequest(filter);
        log.info("\n\n\nFilter:\n" + filter.toString());
        Page<Food> page = filterWithDynamicQuery(Food.class, filterRequest);
        log.info("Response: " + page.getContent().toString());
        return page;
    }

    private FilterRequest toFilterRequest(FoodFilter filter) {
        FilterRequest request = new FilterRequest();

        // exact
        Map<String, Object> exact = new HashMap<>();
        exact.put("available", filter.getAvailable() == null ? true : filter.getAvailable());
        if (filter.getCategoryId() != null) {
            exact.put("categoryId", filter.getCategoryId());
        }
        request.setExact(exact);

        // regex
        if (filter.getName() != null && !filter.getName().isBlank()) {
            request.setRegex(Map.of("name", filter.getName()));
        }

        // range
        if (filter.getMinPrice() != null && filter.getMaxPrice() != null) {
            request.setRange(Map.of(
                "basePrice", new FilterRequest.RangeRequest(filter.getMinPrice(), filter.getMaxPrice())
            ));
        }

        request.setPage(filter.getPage());
        request.setSize(filter.getSize());
        return request;
    }


}
