package org.dehorsed.microservices.product.service;

import java.util.List;
import java.util.NoSuchElementException;

import org.dehorsed.microservices.product.dto.ProductRequest;
import org.dehorsed.microservices.product.dto.ProductResponse;
import org.dehorsed.microservices.product.mapper.ProductMapper;
import org.dehorsed.microservices.product.repository.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductResponse create(ProductRequest productRequest) {
        return productMapper.toResponseDto(productRepository.save(productMapper.toEntity(productRequest)));
    }

    public List<ProductResponse> getAll() {
        return productRepository.findAll().stream().map((productEntity) -> productMapper.toResponseDto(productEntity))
                .toList();
    }

    public ProductResponse getById(String id) {
        return productMapper.toResponseDto(
                productRepository.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("User with id " + id + " not found")));
    }
}
