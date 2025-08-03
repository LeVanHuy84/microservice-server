package com.huyle.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.huyle.dtos.CatagoryDtos.CatagoryRequest;
import com.huyle.dtos.CatagoryDtos.CatagoryResponse;
import com.huyle.mappers.CatagoryMapper;
import com.huyle.models.Catagory;
import com.huyle.repositories.CatagoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CatagoryService {
    private final CatagoryRepository catagoryRepository;
    private final CatagoryMapper catagoryMapper;

    public List<CatagoryResponse> getCatagories() {
        return catagoryMapper.toDTOs(catagoryRepository.findAll());
    }

    public void createCatagory(CatagoryRequest Catagory) {
        Catagory catagoryEntity = catagoryMapper.toEntity(Catagory);
        catagoryRepository.save(catagoryEntity);
    }

    public void updateCatagory(String id, CatagoryRequest Catagory) {
        try {
            Catagory catagoryEntity = catagoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Food not found with id: " + id));
            catagoryMapper.updateEntityFromDto(Catagory, catagoryEntity);
            catagoryRepository.save(catagoryEntity);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Optional<CatagoryResponse> getCatagoryById(String id) {
        return catagoryRepository.findById(id)
            .map(catagoryMapper::toDTO);
    }

    public void deleteCatagory(String id) {
        catagoryRepository.deleteById(id);
    }
}
