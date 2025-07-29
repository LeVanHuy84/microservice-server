package com.huyle.common.mongo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.huyle.dtos.FilterRequest;

public class MongoQueryBuilder {

    public static Query build(FilterRequest filterRequest) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // exact match
        if (filterRequest.getExact() != null) {
            filterRequest.getExact().forEach((key, value) -> {
                criteriaList.add(Criteria.where(key).is(value));
            });
        }

        // regex (contains)
        if (filterRequest.getRegex() != null) {
            filterRequest.getRegex().forEach((key, value) -> {
                criteriaList.add(Criteria.where(key).regex(value, "i"));
            });
        }

        // in list
        if (filterRequest.getIn() != null) {
            filterRequest.getIn().forEach((key, value) -> {
                criteriaList.add(Criteria.where(key).in(value));
            });
        }

        // range (gte/lte)
        if (filterRequest.getRange() != null) {
            filterRequest.getRange().forEach((key, range) -> {
                Criteria c = Criteria.where(key);
                if (range.getFrom() != null && range.getTo() != null) {
                    c.gte(range.getFrom()).lte(range.getTo());
                } else if (range.getFrom() != null) {
                    c.gte(range.getFrom());
                } else if (range.getTo() != null) {
                    c.lte(range.getTo());
                }
                criteriaList.add(c);
            });
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        // paging
        Pageable pageable = PageRequest.of(
                filterRequest.getPage(),
                filterRequest.getSize(),
                Sort.by(Sort.Direction.fromString(filterRequest.getSortDir()), filterRequest.getSortBy())
        );
        query.with(pageable);

        return query;
    }
}
