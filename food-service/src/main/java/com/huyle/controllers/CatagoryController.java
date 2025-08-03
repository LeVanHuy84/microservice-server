package com.huyle.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.huyle.dtos.CatagoryDtos.CatagoryRequest;
import com.huyle.dtos.CatagoryDtos.CatagoryResponse;
import com.huyle.service.CatagoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catagories")
@RequiredArgsConstructor
public class CatagoryController {
    private final CatagoryService catagoryService;

    @GetMapping()
    public List<CatagoryResponse> getCatagories() {
        return catagoryService.getCatagories();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createCatagory(@RequestBody CatagoryRequest catagory) {
        catagoryService.createCatagory(catagory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CatagoryResponse>> getCatagoryById(@PathVariable String id) {
        return ResponseEntity.ok(catagoryService.getCatagoryById(id));
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public void updateCatagory(@PathVariable String id, @RequestBody CatagoryRequest catagory) {
        catagoryService.updateCatagory(id, catagory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatagory(@PathVariable String id) {
        catagoryService.deleteCatagory(id);
        return ResponseEntity.noContent().build();
    }
}
