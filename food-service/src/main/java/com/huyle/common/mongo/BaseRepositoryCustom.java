package com.huyle.common.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.huyle.dtos.FilterRequest;

public abstract class BaseRepositoryCustom<T> {

    @Autowired
    protected MongoTemplate mongoTemplate;

    public Page<T> filterWithDynamicQuery(Class<T> clazz, FilterRequest filterRequest) {
        Query query = MongoQueryBuilder.build(filterRequest);
        long total = mongoTemplate.count(query, clazz);
        List<T> content = mongoTemplate.find(query, clazz);
        Pageable pageable = PageRequest.of(filterRequest.getPage(), filterRequest.getSize());
        return new PageImpl<>(content, pageable, total);
    }
}

