package org.dehorsed.productservice.mapper;

import org.dehorsed.productservice.dto.ProductRequest;
import org.dehorsed.productservice.dto.ProductResponse;
import org.dehorsed.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest productRequest);

    ProductResponse toResponseDto(Product user);
}
