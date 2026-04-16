package org.dehorsed.microservices.product.mapper;

import org.dehorsed.microservices.product.dto.ProductRequest;
import org.dehorsed.microservices.product.dto.ProductResponse;
import org.dehorsed.microservices.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequest productRequest);

    ProductResponse toResponse(Product user);
}
