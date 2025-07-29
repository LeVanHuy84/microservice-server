package com.huyle.models;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private BigDecimal basePrice;
    private List<ImageInfo> images;
    private String categoryId;
    private String restaurantId;

    @Builder.Default
    private boolean available = true;

}
