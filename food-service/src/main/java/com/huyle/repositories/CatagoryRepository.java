package com.huyle.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.huyle.models.Catagory;


public interface CatagoryRepository extends MongoRepository<Catagory, String> {
}

