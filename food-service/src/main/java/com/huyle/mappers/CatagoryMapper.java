package com.huyle.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.huyle.dtos.FoodDtos.CatagoryRequest;
import com.huyle.dtos.FoodDtos.CatagoryResponse;
import com.huyle.mapper.GenericMapper;
import com.huyle.models.Catagory;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface CatagoryMapper extends GenericMapper<CatagoryRequest, Catagory, CatagoryResponse> {
    // This interface can be used to define additional mapping methods specific to Menu if needed

}
