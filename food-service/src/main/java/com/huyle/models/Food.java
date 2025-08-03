package com.huyle.models;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.huyle.utils.ImageInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(value = "foods")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Food {
    @Id
    private String id;
    private String name;
    private String description;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal basePrice;
    
    private List<ImageInfo> images;
    private String categoryId;
    private String restaurantId;

    @Builder.Default
    private boolean available = true;

    private double totalRating;

    @Builder.Default
    private int totalFeedback = 0;

    @Builder.Default
    private int totalOrder = 0;
}
