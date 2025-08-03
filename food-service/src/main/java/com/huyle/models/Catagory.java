package com.huyle.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "catagories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Catagory {
    @Id
    private String id;
    private String name;
}

